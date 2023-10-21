package com.heima.article.job;

import com.heima.article.service.HotArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fzj
 * @date 2023-08-31 22:45
 */
@Component
@Slf4j
public class ComputeHotArticleJob {
    @Autowired
    private HotArticleService hotArticleService;

    @XxlJob("computeHotArticleJob")
    public void handle(){
        log.info("热文章分值计算调度任务开始");
        hotArticleService.computerHotArticle();
        log.info("热文章分值计算调度任务结束");
    }
}
