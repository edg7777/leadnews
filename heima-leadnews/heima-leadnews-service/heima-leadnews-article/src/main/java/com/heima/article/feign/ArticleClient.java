package com.heima.article.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleConfigService;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dto.ArticleCommentDto;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.article.pojo.ApArticleConfig;
import com.heima.model.comment.dtos.CommentConfigDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.StatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author fzj
 * @date 2023-08-22 11:51
 */
@RestController
@FeignClient
public class ArticleClient implements IArticleClient {
    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/api/v1/article/save")
    @Override
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArticle(dto);
    }

    @GetMapping("/api/v1/article/queryLikesAndConllections")
    @Override
    public ResponseResult queryLikesAndConllections(@RequestParam("wmUserId") Integer wmUserId, @RequestParam("beginDate") Date beginDate, @RequestParam("endDate") Date endDate) {
        return apArticleService.queryLikesAndConllections(wmUserId,beginDate,endDate);
    }

    @PostMapping("/api/v1/article/newPage")
    @Override
    public PageResponseResult newPage(@RequestBody StatisticsDto dto) {
        return apArticleService.newPage(dto);
    }

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @GetMapping("/api/v1/article/findArticleConfigByArticleId/{articleId}")
    @Override
    public ResponseResult findArticleConfigByArticleId(@PathVariable("articleId") Long articleId) {
        ApArticleConfig apArticleConfig = apArticleConfigService.getOne(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId, articleId));
        return ResponseResult.okResult(apArticleConfig);
    }

    @PostMapping("/api/v1/article/findNewsComments")
    @Override
    public PageResponseResult findNewsComments(@RequestBody ArticleCommentDto dto) {
        return apArticleService.findNewsComments(dto);
    }

    @PostMapping("/api/v1/article/updateCommentStatus")
    @Override
    public ResponseResult updateCommentStatus(@RequestBody CommentConfigDto dto) {
        return apArticleConfigService.updateCommentStatus(dto);
    }
}
