package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author fzj
 * @date 2023-08-21 11:18
 */
public interface WmNewsService extends IService<WmNews> {

    /**
     * 条件查询文章列表
     * @param dto
     * @return
     */
    public ResponseResult findList( WmNewsPageReqDto dto);

    /**
     * 上传文章
     * @param dto
     * @return
     */
    public ResponseResult submitNews(@RequestBody WmNewsDto dto);

    /**
     * 上下架文章
     * @param dto
     * @return
     */
    public ResponseResult downOrUp(WmNewsDto dto);
}
