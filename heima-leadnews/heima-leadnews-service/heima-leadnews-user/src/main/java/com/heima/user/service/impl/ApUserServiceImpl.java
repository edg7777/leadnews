package com.heima.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dto.LoginDTO;
import com.heima.model.user.dto.UserDTO;
import com.heima.model.user.pojo.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fzj
 * @date 2023-08-10 11:33
 */
@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {


    /**
     * app端登录功能
     * @param loginDTO
     * @return
     */
    @Override
    public ResponseResult login(LoginDTO loginDTO) {
        //1.正常登录，用户名和密码
        if(StringUtils.isNotBlank(loginDTO.getPhone())&&StringUtils.isNotBlank(loginDTO.getPassword())){
            //1.1根据手机号来查询用户信息
            ApUser user = query().eq("phone", loginDTO.getPhone()).one();
            if(user==null){
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户信息不存在");
            }
            //1.2比对密码
            String salt = user.getSalt();
            String password = loginDTO.getPassword();
            String finalPassword = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if(!finalPassword.equals(user.getPassword())){
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            //1.3返回数据jwt
            String token = AppJwtUtil.getToken(user.getId().longValue());
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            Map<String,Object> map=new HashMap<>();
            map.put("token",token);
            map.put("user",userDTO);
            return ResponseResult.okResult(map);
        }
        else{
            //2.游客登录
            Map<String,Object> map=new HashMap<>();
            map.put("token",AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }
}
