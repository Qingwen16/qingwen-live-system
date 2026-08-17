package com.wen.model.vo;

import lombok.Data;

/**
 * 设置用户角色请求
 *
 * @author : rjw
 */
@Data
public class RoleSetRequest {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 角色类型 {@link com.wen.common.enums.RoleTypeEnum} 0-用户 1-主播 2-管理员 3-系统管理员
     */
    private Integer role;

}
