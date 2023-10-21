package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.HistorySearchDto;

/**
 * @author fzj
 * @date 2023-08-30 23:21
 */
public interface ApUserSearchService {
    /**
     * 保存用户搜索历史记录
     * @param keyword
     * @param userId
     */
    public void insert(String keyword,Integer userId);

    /**
     * 查询搜索历史
     * @return
     */
    public ResponseResult findUserSearch();

    /**
     * 删除历史记录
     * @param dto
     * @return
     */
    public ResponseResult delUserSearch(HistorySearchDto dto);
}
