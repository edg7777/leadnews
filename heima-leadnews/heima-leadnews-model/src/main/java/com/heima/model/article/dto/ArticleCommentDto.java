package com.heima.model.article.dto;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class ArticleCommentDto extends PageRequestDto {

    private String beginDate;
    private String endDate;
    private Integer wmUserId;
}
