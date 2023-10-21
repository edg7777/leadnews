package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dto.ArticleCommentDto;
import com.heima.model.article.dto.ArticleHomeDTO;
import com.heima.model.article.pojo.ApArticle;
import com.heima.model.article.vos.ArticleCommnetVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author fzj
 * @date 2023-08-12 22:22
 */
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    /**
     * 加载文章列表
     * @param articleHomeDTO
     * @param type 1=加载更多  2=加载最新
     * @return
     */
    public List<ApArticle> loadArticleList(ArticleHomeDTO articleHomeDTO,Short type);


    public List<ApArticle> findArticleListByLast5Days(@Param("dayParam") Date dayParam);

    Map queryLikesAndConllections(Integer wmUserId, Date beginDate, Date endDate);

    List<ArticleCommnetVo> findNewsComments(ArticleCommentDto dto);

    int findNewsCommentsCount(ArticleCommentDto dto);
}
