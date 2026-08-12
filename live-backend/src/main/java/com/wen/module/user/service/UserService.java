package com.wen.module.user.service;

import com.wen.module.user.domain.vo.UserQueryRequest;
import com.wen.module.user.domain.entity.UserInfo;
import com.wen.module.user.domain.vo.UserInfoDto;

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
    UserInfoDto registerUser(String phone);

    /**
     * 根据 查询条件 获取用户信息
     */
    List<UserInfoDto> queryUserByCondition(UserQueryRequest request);

    /**
     * 根据 手机号 获取用户信息
     */
    UserInfoDto queryByPhone(String phone);

    /**
     * 根据 用户ID 获取用户信息
     */
    UserInfo queryByUserId(Long userId);

    /**
     * 根据用户ID批量查询用户信息
     */
    List<UserInfo> queryByUserIdSet(Set<Long> userIdSet);
}
