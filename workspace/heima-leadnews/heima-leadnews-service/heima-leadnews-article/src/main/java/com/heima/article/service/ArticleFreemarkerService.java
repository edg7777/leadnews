package com.heima.article.service;

import com.heima.model.article.pojo.ApArticle;

/**
 * @author fzj
 * @date 2023-08-22 21:21
 */
public interface ArticleFreemarkerService {
    /**
     * 生成静态文件上传到minIo中
     * @param apArticle
     * @param content
     */
    void buildArticleToMinIO(ApArticle apArticle,String content);
}
