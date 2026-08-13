package com.wen.model.vo;

import com.wen.model.dto.TokenDto;
import com.wen.model.dto.UserDto;
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
    private UserDto userDto;

}
