package com.wen.service;

import com.wen.model.vo.PhoneRequest;
import com.wen.model.vo.UserIdRequest;
import com.wen.model.vo.UserGetRequest;
import com.wen.model.entity.UserEntity;
import com.wen.model.dto.UserDto;
import com.wen.common.response.PageResult;

import java.util.List;
import java.util.Set;

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
    PageResult<UserDto> queryByCondition(UserGetRequest request);

    /**
     * 根据 手机号 获取用户信息
     */
    UserDto queryByPhone(PhoneRequest request);

    /**
     * 根据 用户ID 获取用户信息
     */
    UserEntity queryByUserId(Long userId);

    /**
     * 根据用户ID批量查询用户信息
     */
    List<UserEntity> queryByUserIdSet(Set<Long> userIdSet);

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
