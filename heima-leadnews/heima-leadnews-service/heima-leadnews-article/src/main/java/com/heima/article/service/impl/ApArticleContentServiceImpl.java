package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleContentService;
import com.heima.model.article.pojo.ApArticleContent;
import org.springframework.stereotype.Service;

/**
 * @author fzj
 * @date 2023-08-14 14:37
 */
@Service
public class ApArticleContentServiceImpl extends ServiceImpl<ApArticleContentMapper, ApArticleContent> implements ApArticleContentService {
}
