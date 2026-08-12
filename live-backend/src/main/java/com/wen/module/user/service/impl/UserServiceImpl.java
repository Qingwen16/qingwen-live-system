package com.wen.module.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.enums.StatusEnum;
import com.wen.common.exception.BusinessException;
import com.wen.common.generator.UserIdGenerator;
import com.wen.module.user.domain.vo.UserInfoDto;
import com.wen.module.user.mapper.UserRoleMapper;
import com.wen.module.user.domain.vo.UserQueryRequest;
import com.wen.module.user.domain.entity.UserInfo;
import com.wen.module.user.domain.entity.UserRole;
import com.wen.module.user.mapper.UserInfoMapper;
import com.wen.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @Author : 青灯文案
 * @Date: 2026/3/14
 * 用户服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserInfoMapper userInfoMapper;

    private final UserRoleMapper userRoleMapper;

    /**
     * 创建通过第三方软件登录的用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoDto registerUser(String phone) {
        UserInfoDto userInfoDto = queryByPhone(phone);
        // 1. 存在信息直接返回
        if (userInfoDto != null) {
            log.info("手机号注册，该用户存在用户信息: {}", userInfoDto);
            return userInfoDto;
        }
        // 2. 新用户自动注册
        log.info("手机号用户注册：{}", phone);
        long currentTime = System.currentTimeMillis();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(UserIdGenerator.generator());
        userInfo.setUsername("phone_" + phone);
        userInfo.setPhone(phone);
        userInfo.setStatus(StatusEnum.NORMAL.getCode());
        userInfo.setDeleted(DeleteEnum.ACTIVE.getCode());
        userInfo.setCreateTime(currentTime);
        userInfo.setUpdateTime(currentTime);
        userInfoMapper.insert(userInfo);
        // 3. 设置用户角色
        UserRole userRole = new UserRole();
        userRole.setUserId(userInfo.getUserId());
        userRole.setUserName(userInfo.getUsername());
        userRole.setPhone(userInfo.getPhone());
        userRole.setRole(RoleTypeEnum.USER.getCode());
        userRole.setCreateTime(currentTime);
        userRole.setUpdateTime(currentTime);
        userRoleMapper.insert(userRole);
        log.info("手机号用户注册成功：createUser={}", userInfo);
        // 4. 构建返回类型
        UserInfoDto response = new UserInfoDto();
        BeanUtil.copyProperties(userInfo, response);
        return response;
    }

    @Override
    public List<UserInfoDto> queryUserByCondition(UserQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StrUtil.isEmpty(request.getUsername()), UserInfo::getUsername, request.getUsername())
                .eq(!StrUtil.isEmpty(request.getPhone()), UserInfo::getPhone, request.getPhone())
                .eq(request.getGender() != null, UserInfo::getGender, request.getGender())
                .eq(!StrUtil.isEmpty(request.getCountry()), UserInfo::getCountry, request.getCountry())
                .eq(!StrUtil.isEmpty(request.getProvince()), UserInfo::getProvince, request.getProvince())
                .eq(!StrUtil.isEmpty(request.getCity()), UserInfo::getCity, request.getCity())
                .eq(request.getStatus() != null, UserInfo::getStatus, request.getStatus())
                .eq(request.getDeleted() != null, UserInfo::getDeleted, request.getDeleted())
                .orderByDesc(UserInfo::getCreateTime);
        // 查询用户列表
        List<UserInfo> userInfoList = userInfoMapper.selectList(wrapper);

        // 转换为 DTO
        List<UserInfoDto> dtoList = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            UserInfoDto dto = new UserInfoDto();
            BeanUtils.copyProperties(userInfo, dto);
            dtoList.add(dto);
        }

        log.info("根据条件查询到的用户信息数量: [{}]", dtoList.size());
        return dtoList;
    }

    @Override
    public UserInfoDto queryByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new BusinessException("输入参数不能为空");
        }
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getPhone, phone));
        UserInfoDto response = new UserInfoDto();
        BeanUtil.copyProperties(userInfo, response);
        log.info("根据手机号 [{}] 查询用户成功 [{}]", phone, response);
        return response;
    }

    @Override
    public UserInfo queryByUserId(Long userId) {
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUserId, userId));
        log.info("根据用户ID [{}] 查询用户成功 [{}]", userId, userInfo);
        return userInfo;
    }

    @Override
    public List<UserInfo> queryByUserIdSet(Set<Long> userIdSet) {
        if (CollectionUtils.isEmpty(userIdSet)) {
            log.info("输入的查询用户ID数量为空");
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getUserId, userIdSet);
        List<UserInfo> infoList = userInfoMapper.selectList(wrapper);
        log.info("查询到的用户信息数量: [{}]", infoList.size());
        return infoList;
    }
}