package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dto.ArticleCommentDto;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.article.dto.ArticleHomeDTO;
import com.heima.model.article.dto.ArticleInfoDto;
import com.heima.model.article.pojo.ApArticle;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.wemedia.dtos.StatisticsDto;

import java.util.Date;

/**
 * @author fzj
 * @date 2023-08-12 22:27
 */
public interface ApArticleService extends IService<ApArticle> {

    /**
     * 加载文章列表
     * @param articleHomeDTO
     * @param type
     * @return
     */
    ResponseResult load(ArticleHomeDTO articleHomeDTO,Short type);

    /**
     * 保存app端相关文章
     * @param dto
     * @return
     */
    public ResponseResult saveArticle(ArticleDto dto);

    /**
     * 加载文章列表
     * @param dto
     * @param type
     * @param firstPage true 是首页 false 不是首页
     * @return
     */
    public ResponseResult load2(ArticleHomeDTO dto,Short type,boolean firstPage);

    /**
     * 加载文章详情 数据回显
     * @param dto
     * @return
     */
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto);



    /**
     * 图文统计统计
     * @param wmUserId
     * @param beginDate
     * @param endDate
     * @return
     */
    ResponseResult queryLikesAndConllections(Integer wmUserId, Date beginDate, Date endDate);

    /**
     * 分页查询 图文统计
     * @param dto
     * @return
     */
    PageResponseResult newPage(StatisticsDto dto);

    /**
     * 查询文章评论统计
     * @param dto
     * @return
     */
    public PageResponseResult findNewsComments(ArticleCommentDto dto);

    /**
     * 更新文章的分值  同时更新缓存中的热点文章数据
     * @param mess
     */
    public void updateScore(ArticleVisitStreamMess mess);
}
