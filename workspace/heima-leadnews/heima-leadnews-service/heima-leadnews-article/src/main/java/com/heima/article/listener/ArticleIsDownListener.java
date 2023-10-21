package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApArticleConfigService;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.model.article.pojo.ApArticle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author fzj
 * @date 2023-08-29 22:02
 */
@Component
@Slf4j
public class ArticleIsDownListener {
    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @KafkaListener(topics=WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void onMessage(String message){
        if (StringUtils.isNotBlank(message)) {
            Map map = JSON.parseObject(message, Map.class);
            apArticleConfigService.updateByMap(map);
        }

    }
}
