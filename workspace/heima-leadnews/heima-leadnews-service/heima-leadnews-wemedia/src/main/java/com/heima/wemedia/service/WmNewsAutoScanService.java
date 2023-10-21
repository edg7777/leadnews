package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.wemedia.pojos.WmNews;

/**
 * @author fzj
 * @date 2023-08-22 15:27
 */
public interface WmNewsAutoScanService{
    /**
     * 自媒体文章审核
     * @param id 自媒体文章id
     */
    public void autoScanNews(Integer id);
}
