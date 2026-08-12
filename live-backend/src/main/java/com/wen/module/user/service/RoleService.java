package com.wen.module.user.service;

import com.wen.module.user.domain.vo.UserRoleDto;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface RoleService {

    /**
     * 根据类型查询所用角色
     */
    List<UserRoleDto> queryRole(List<Integer> types);

    /**
     * 设置超级管理员
     */
    String setAdmin(String phone, String code);

    /**
     * 设置房管
     */
    String setRoomAdminRole(String phone);

    /**
     * 设置主播
     */
    String setAnchorRole(String phone);

    /**
     * 设置用户
     */
    String setUserRole(String phone);

}
