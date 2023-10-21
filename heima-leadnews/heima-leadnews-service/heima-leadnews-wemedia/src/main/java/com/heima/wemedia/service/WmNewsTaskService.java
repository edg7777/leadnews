package com.heima.wemedia.service;

import java.util.Date;

/**
 * @author fzj
 * @date 2023-08-28 14:20
 */
public interface WmNewsTaskService {

    /**
     * 添加任务到延迟队列中
     * @param id
     * @param publishTime
     */
    public void addNewsToTask(Integer id, Date publishTime);

    /**
     * 消费任务
     */
    public void scanNewsByTask();
}
