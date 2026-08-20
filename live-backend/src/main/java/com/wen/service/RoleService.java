package com.wen.service;

import com.wen.common.response.PageResult;
import com.wen.model.dto.RoleDto;
import com.wen.model.vo.RoleGetRequest;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface RoleService {

    /**
     * 分页查询用户角色
     */
    PageResult<RoleDto> queryRole(RoleGetRequest request);

    /**
     * 根据用户 ID 查询角色类型
     */
    Integer queryRoleByUserId(Long userId);

    /**
     * 设置用户角色（存在则更新、不存在则插入）
     */
    void setRole(Long userId, Integer role);

}
