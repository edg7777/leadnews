package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author fzj
 * @date 2023-08-28 10:08
 */
@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskinfoMapper taskinfoMapper;

    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;


    /**
     * 添加延时任务
     *
     * @param task
     * @return
     */
    @Override
    public long addTask(Task task) {

        //添加任务到数据库中
        int success= addTaskToDb(task);
        if(success!=0){
            //添加任务到redis
            addTaskToRedis(task);
        }
        return task.getTaskId();
    }
    @Autowired
    private CacheService cacheService;
    /**
     * 把任务添加到redis中
     * @param task
     */
    private void addTaskToRedis(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();

        //获取5分钟之后的时间  毫秒值
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();

        //2.1 如果任务的执行时间小于等于当前时间，存入list
        long currentTime = System.currentTimeMillis();
        if (task.getExecuteTime() <= currentTime) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            //2.2 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟） 存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSON.toJSONString(task), task.getExecuteTime());
        }
    }

    /**
     * 添加任务到数据库中
     * @param task
     * @return
     */
    private int addTaskToDb(Task task) {
        //保存任务表
        Taskinfo taskinfo=new Taskinfo();
        BeanUtils.copyProperties(task,taskinfo);
        taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
        taskinfoMapper.insert(taskinfo);
        task.setTaskId(taskinfo.getTaskId());
        //保存任务日志表
        TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
        BeanUtils.copyProperties(taskinfo,taskinfoLogs);
        taskinfoLogs.setVersion(1);
        taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
        return taskinfoLogsMapper.insert(taskinfoLogs);
    }

    /**
     * 取消任务
     *
     * @param taskId
     * @return
     */
    @Override
    public boolean cancelTask(long taskId) {
        //删除任务更新任务日志
        Task task= updateDb(taskId,ScheduleConstants.CANCELLED);
        //删除redis中的数据
        removeTaskFromRedis(task);
        return true;
    }

    /**
     * 删除redis中的数据
     * @param task
     */
    private void removeTaskFromRedis(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();
        if(task.getExecuteTime()<=System.currentTimeMillis()){
            cacheService.lRemove(ScheduleConstants.TOPIC+key,0,JSON.toJSONString(task));
        }
        else{
            cacheService.zRemove(ScheduleConstants.FUTURE,JSON.toJSONString(task));
        }
    }

    /**
     * 删除任务，更新任务日志
     * @param taskId
     * @param status
     * @return
     */
    private Task updateDb(long taskId, int status) {
        taskinfoMapper.deleteById(taskId);
        TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
        taskinfoLogs.setStatus(status);
        taskinfoLogsMapper.updateById(taskinfoLogs);
        Task task = new Task();
        BeanUtils.copyProperties(taskinfoLogs,task);
        task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        return task;
    }

    /**
     * 按照类型和优先级拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    @Override
    public Task poll(int type, int priority) {
        String key=type+"_"+priority;
        //从redis中拉取数据
        String task_json=cacheService.lRightPop(ScheduleConstants.TOPIC+key);
        Task task = new Task();
        if(StringUtils.isNotBlank(task_json)){
            task = JSON.parseObject(task_json, Task.class);
            //修改数据库信息
            updateDb(task.getTaskId(),ScheduleConstants.EXECUTED);
        }
        return task;
    }

    /**
     * 未来数据定时刷新
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh(){
        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);
        if(StringUtils.isNotBlank(token)){
            log.info("未来数据定时刷新");
            //获取所有未来数据的集合key
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) {//future
                //获取当前数据的key
                String topicKey=ScheduleConstants.TOPIC+futureKey.split(ScheduleConstants.FUTURE)[1];
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                //同步数据
                if(!tasks.isEmpty()){
                    cacheService.refreshWithPipeline(futureKey,topicKey,tasks);
                    log.info("成功的将"+futureKey+"刷新到了"+topicKey);
                }
            }
        }
    }

    /**
     * 数据库任务定时同步到redis中
     */
    @PostConstruct
    @Scheduled(cron = "0 */5 * * * ?")
    public void reloadData(){
        //清理缓存
        clearCache();
        //查询符合条件的任务小于未来5分钟的数据
        //获取5分钟之后的时间  毫秒值
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        List<Taskinfo> taskinfoList = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery().lt(Taskinfo::getExecuteTime, calendar.getTime()));
        if(taskinfoList!=null &&taskinfoList.size()>0){
            for (Taskinfo taskinfo : taskinfoList) {
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo,task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                addTaskToRedis(task);
            }
        }
        log.info("数据库的任务同步到了redis中");
        //把任务添加到redis
    }

    /**
     * 清除缓存
     */
    public void clearCache(){
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        cacheService.delete(topicKeys);
        cacheService.delete(futureKeys);
    }
}
