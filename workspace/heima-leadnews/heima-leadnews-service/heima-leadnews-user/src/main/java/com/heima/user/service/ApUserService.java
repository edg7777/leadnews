package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dto.LoginDTO;
import com.heima.model.user.pojo.ApUser;
import com.heima.user.mapper.ApUserMapper;
import org.springframework.stereotype.Service;

/**
 * @author fzj
 * @date 2023-08-10 11:31
 */

public interface ApUserService extends IService<ApUser> {
    /**
     * app端登录功能
     * @param loginDTO
     * @return
     */
    public ResponseResult login(LoginDTO loginDTO);
}
