package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author fzj
 * @date 2023-08-21 10:18
 */
public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     * @return
     */
    public ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 查看自己素材列表
     * @param dto
     * @return
     */
    public ResponseResult findList(WmMaterialDto dto);
}
