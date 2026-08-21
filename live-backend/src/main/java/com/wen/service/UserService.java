package com.wen.service;

import com.wen.model.vo.UserIdRequest;
import com.wen.model.vo.UserQueryRequest;
import com.wen.model.entity.UserEntity;
import com.wen.model.dto.UserDto;
import com.wen.common.response.PageResult;

/**
 * 用户服务接口
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
public interface UserService {

    /**
     * 用户注册（手机等第三方注册）
     */
    UserDto registerUser(String phone);

    /**
     * 根据 查询条件 分页获取用户信息（含角色）
     */
    PageResult<UserDto> queryByCondition(UserQueryRequest request);

    /**
     * 根据 手机号 获取用户信息
     */
    UserDto queryByPhone(String phone);

    /**
     * 根据 用户ID 获取用户信息
     */
    UserEntity queryByUserId(Long userId);

    /**
     * 禁用用户
     */
    void disableUser(UserIdRequest request);

    /**
     * 启用用户
     */
    void enableUser(UserIdRequest request);

    /**
     * 注销用户（软删除）
     */
    void deleteUser(UserIdRequest request);
}
