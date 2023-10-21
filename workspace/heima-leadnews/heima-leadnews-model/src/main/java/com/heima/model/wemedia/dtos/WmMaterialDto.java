package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @author fzj
 * @date 2023-08-21 10:40
 */
@Data
public class WmMaterialDto extends PageRequestDto {
    /**
     * 0为不收藏，1为收藏
     */
    private Short isCollection;
}
