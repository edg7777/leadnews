package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.pojo.ApArticleConfig;
import com.heima.model.comment.dtos.CommentConfigDto;
import com.heima.model.common.dtos.ResponseResult;

import java.util.Map;

/**
 * @author fzj
 * @date 2023-08-29 22:04
 */
public interface ApArticleConfigService extends IService<ApArticleConfig> {
    void updateByMap(Map map);

    public ResponseResult updateCommentStatus(CommentConfigDto dto);
}
