package com.wen.module.user.domain.vo;

import com.wen.module.auth.domain.vo.TokenDto;
import lombok.Data;

/**
 * 用户信息响应 DTO
 */
@Data
public class UserInfoResponse {

    /**
     * Token
     */
    private TokenDto tokenDto;

    /**
     * 用户 ID
     */
    private UserInfoDto userInfoDto;

}
