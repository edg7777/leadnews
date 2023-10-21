package com.heima.model.article.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author fzj
 * @date 2023-08-12 22:19
 */
@Data
public class ArticleHomeDTO {
    // 最大时间
    Date maxBehotTime;
    // 最小时间
    Date minBehotTime;
    // 分页size
    Integer size;
    // 频道ID
    String tag;
}
