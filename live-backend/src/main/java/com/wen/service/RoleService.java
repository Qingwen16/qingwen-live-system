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
     * 设置管理员（手机端平台运营）
     */
    String setAdmin(String phone, String code);

    /**
     * 设置系统管理员（管理端最高权限）
     */
    String setSuperAdmin(String phone, String code);

    /**
     * 设置主播
     */
    String setAnchorRole(String phone);

    /**
     * 设置用户
     */
    String setUserRole(String phone);

}
