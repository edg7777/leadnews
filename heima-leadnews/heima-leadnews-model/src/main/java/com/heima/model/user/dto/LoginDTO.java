package com.heima.model.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fzj
 * @date 2023-08-10 11:28
 */
@Data
public class LoginDTO {
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号",required = true)
    private String phone;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码",required = true)
    private String password;
}
