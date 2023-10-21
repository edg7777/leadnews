package com.heima.apis.article;

import com.heima.model.article.dto.ArticleCommentDto;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.comment.dtos.CommentConfigDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.StatisticsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author fzj
 * @date 2023-08-22 11:48
 */

@FeignClient(value = "leadnews-article")
public interface IArticleClient {

    @PostMapping("api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody  ArticleDto dto);

    @GetMapping("/api/v1/article/queryLikesAndConllections")
    ResponseResult queryLikesAndConllections(@RequestParam("wmUserId") Integer wmUserId, @RequestParam("beginDate") Date beginDate, @RequestParam("endDate") Date endDate);

    @PostMapping("/api/v1/article/newPage")
    PageResponseResult newPage(@RequestBody StatisticsDto dto);

    @GetMapping("/api/v1/article/findArticleConfigByArticleId/{articleId}")
    ResponseResult findArticleConfigByArticleId(@PathVariable("articleId") Long articleId);

    @PostMapping("/api/v1/article/findNewsComments")
    public PageResponseResult findNewsComments(@RequestBody ArticleCommentDto dto);

    @PostMapping("/api/v1/article/updateCommentStatus")
    public ResponseResult updateCommentStatus(@RequestBody CommentConfigDto dto);
}
