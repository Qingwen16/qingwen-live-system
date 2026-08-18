package com.wen.service;

import com.wen.model.dto.RoleDto;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface RoleService {

    /**
     * 根据类型查询所用角色
     */
    List<RoleDto> queryRole(List<Integer> types);

    /**
     * 根据用户 ID 查询角色类型
     */
    Integer queryRoleByUserId(Long userId);

    /**
     * 设置用户角色
     *
     * @param userId 用户ID
     * @param role   角色类型 code，取值见 {@link com.wen.common.enums.RoleTypeEnum}
     */
    String setRole(Long userId, Integer role);

}
