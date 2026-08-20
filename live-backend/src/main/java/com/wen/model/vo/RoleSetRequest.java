package com.wen.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设置用户角色请求
 *
 * @author : rjw
 */
@Data
public class RoleSetRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色类型 {@link com.wen.common.enums.RoleTypeEnum} 0-用户 1-主播 2-管理员 3-系统管理员
     */
    @NotNull(message = "角色类型不能为空")
    private Integer role;

}
