package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

import java.io.IOException;

/**
 * @author fzj
 * @date 2023-08-30 20:41
 */
public interface ArticleSearchService {

    /**
     * es文章分页检索
     * @param dto
     * @return
     */
    ResponseResult search(UserSearchDto dto) throws IOException;


}
