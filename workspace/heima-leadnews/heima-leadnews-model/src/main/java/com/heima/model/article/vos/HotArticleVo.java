package com.heima.model.article.vos;

import com.heima.model.article.pojo.ApArticle;
import lombok.Data;

/**
 * @author fzj
 * @date 2023-08-31 21:49
 */
@Data
public class HotArticleVo extends ApArticle {
    /**
     * 文章分值
     */
    private Integer score;
}
