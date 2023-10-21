package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.TaskTypeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.ProtostuffUtil;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author fzj
 * @date 2023-08-28 14:22
 */
@Service
@Slf4j
@Transactional
public class WmNewsTaskServiceImpl implements WmNewsTaskService{
//    private Timer timer;
//
//    public void startScanTask() {
//        timer = new Timer();
//
//        // 使用schedule方法定时执行任务，初始延迟为0，每隔5分钟执行一次
//        timer.schedule(new ScanNewsTask(), 0, 5 * 60 * 1000);
//    }
//
//    public void stopScanTask() {
//        timer.cancel();
//    }
//
//    private class ScanNewsTask extends TimerTask {
//        @Override
//        public void run() {
//            scanNewsByTask();
//        }
//    }

    @Autowired
    private IScheduleClient scheduleClient;
    /**
     * 添加任务到延迟队列中
     *
     * @param id
     * @param publishTime
     */
    @Async
    @Override
    public void addNewsToTask(Integer id, Date publishTime) {
        log.info("添加队列到延迟服务中");
        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(ProtostuffUtil.serialize(wmNews));
        scheduleClient.addTask(task);
        log.info("添加任务结束");

    }
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 消费延迟队列数据
     */
//    @Scheduled(fixedRate = 1000)
    @Override
    @Async
    public void scanNewsByTask() {
        log.info("文章审核---消费任务执行---begin---");
        ResponseResult responseResult = scheduleClient.poll(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if(responseResult.getCode().equals(200) && responseResult.getData() != null){
            String json_str = JSON.toJSONString(responseResult.getData());
            Task task = JSON.parseObject(json_str, Task.class);
            byte[] parameters = task.getParameters();
            WmNews wmNews = ProtostuffUtil.deserialize(parameters, WmNews.class);
            System.out.println(wmNews.getId()+"-----------");
            wmNewsAutoScanService.autoScanNews(wmNews.getId());
        }
        log.info("文章审核---消费任务执行---end---");
    }
}
