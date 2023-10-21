package com.heima.schedule.test;

import com.alibaba.fastjson.JSON;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author fzj
 * @date 2023-08-26 12:12
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {
    @Test
    public void testZset() {
        /*cacheService.zAdd("zset_key_001","hello,zset1",1000);
        cacheService.zAdd("zset_key_001","hello,zset2",5000);
        cacheService.zAdd("zset_key_001","hello,zset3",6000);
        cacheService.zAdd("zset_key_001","hello,zset4",4000);*/
        Set<String> zsetKey001 = cacheService.zRangeByScore("zset_key_001", 0, 5000);
        System.out.println(zsetKey001);

    }

    @Autowired
    private CacheService cacheService;
    @Test
    public void name() {
        cacheService.lLeftPush("list_001","hello,redis");
        String list001 = cacheService.lRightPop("list_001");
        System.out.println(list001);
    }

    @Test
    public void testKeys() {
        Set<String> keys = cacheService.keys("future_*");
        System.out.println(keys);
        Set<String> scan = cacheService.scan("future_*");
        System.out.println(scan);
    }


    //耗时6151
    @Test
    public  void testPiple1(){
        long start =System.currentTimeMillis();
        for (int i = 0; i <10000 ; i++) {
            Task task = new Task();
            task.setTaskType(1001);
            task.setPriority(1);
            task.setExecuteTime(new Date().getTime());
            cacheService.lLeftPush("1001_1", JSON.toJSONString(task));
        }
        System.out.println("耗时"+(System.currentTimeMillis()- start));
    }


    @Test
    public void testPiple2(){
        long start  = System.currentTimeMillis();
        //使用管道技术
        List<Object> objectList = cacheService.getstringRedisTemplate().executePipelined(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                for (int i = 0; i <10000 ; i++) {
                    Task task = new Task();
                    task.setTaskType(1001);
                    task.setPriority(1);
                    task.setExecuteTime(new Date().getTime());
                    redisConnection.lPush("1001_1".getBytes(), JSON.toJSONString(task).getBytes());
                }
                return null;
            }
        });
        System.out.println("使用管道技术执行10000次自增操作共耗时:"+(System.currentTimeMillis()-start)+"毫秒");
    }
}
