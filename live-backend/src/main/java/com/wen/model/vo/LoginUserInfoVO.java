package com.wen.model.vo;

import com.wen.model.dto.UserDto;
import lombok.Data;

/**
 * 登录响应中的用户摘要信息
 */
@Data
public class LoginUserInfoVO {

    /**
     * 用户唯一标识
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 手机号（脱敏）
     */
    private String phone;

    /**
     * 角色类型 {@link com.wen.common.enums.RoleTypeEnum}
     */
    private Integer role;

    public static LoginUserInfoVO from(UserDto user) {
        LoginUserInfoVO vo = new LoginUserInfoVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setRole(user.getRole());
        return vo;
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
