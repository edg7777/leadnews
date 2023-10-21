package com.heima.wemedia.listener;

import cn.hutool.json.JSONUtil;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.config.DelayedQueueConfig;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fzj
 * @date 2023-09-13 22:21
 */
@Component
@Slf4j
public class DlxListener {
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @RabbitListener(queues = DelayedQueueConfig.DLX_QUEUE_NAME)
    public void onMessage(String msg){
        WmNews wmNews = JSONUtil.toBean(msg, WmNews.class);
        wmNewsAutoScanService.autoScanNews(wmNews.getId());
        System.out.println(wmNews.getId());
    }

}
