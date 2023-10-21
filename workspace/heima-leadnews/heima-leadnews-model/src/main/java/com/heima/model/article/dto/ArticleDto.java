package com.heima.model.article.dto;

import com.heima.model.article.pojo.ApArticle;
import lombok.Data;

/**
 * @author fzj
 * @date 2023-08-22 11:49
 */
@Data
public class ArticleDto extends ApArticle {
    /**
     * 文章内容
     */
    private String content;
}
