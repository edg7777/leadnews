package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dto.ArticleHomeDTO;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fzj
 * @date 2023-08-12 22:17
 */
@RestController
@RequestMapping("api/v1/article")
public class ArticleHomeController {
    @Autowired
    ApArticleService apArticleService;
    /**
     * 加载首页
     * @param articleHomeDTO
     * @return
     */
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDTO articleHomeDTO){
        return apArticleService.load2(articleHomeDTO, ArticleConstants.LOADTYPE_LOAD_MORE,true);

    }

    /**
     * 下拉加载更多文章
     * @param articleHomeDTO
     * @return
     */
    @PostMapping("/loadmore")
    public ResponseResult loadmore(@RequestBody ArticleHomeDTO articleHomeDTO){
        return apArticleService.load(articleHomeDTO, ArticleConstants.LOADTYPE_LOAD_MORE);
    }


    /**
     * 上拉刷新加载最新文章
     * @param articleHomeDTO
     * @return
     */
    @PostMapping("/loadnew")
    public ResponseResult loadnew(@RequestBody ArticleHomeDTO articleHomeDTO){
        return apArticleService.load(articleHomeDTO, ArticleConstants.LOADTYPE_LOAD_NEW);
    }


}
