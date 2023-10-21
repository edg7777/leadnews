package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

/**
 * @author fzj
 * @date 2023-08-28 10:07
 */
public interface TaskService {
    /**
     * 添加延时任务
     * @param task
     * @return
     */
    long addTask(Task task);

    /**
     * 取消任务
     * @param taskId
     * @return
     */
    boolean cancelTask(long taskId);

    /**
     * 按照类型和优先级拉取任务
     * @param type
     * @param priority
     * @return
     */
    Task poll(int type,int priority);
}
