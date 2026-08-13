package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.enums.StatusEnum;
import com.wen.common.exception.BusinessException;
import com.wen.common.generator.UserIdGenerator;
import com.wen.model.dto.UserDto;
import com.wen.mapper.RoleMapper;
import com.wen.model.vo.UserQueryRequest;
import com.wen.model.entity.UserEntity;
import com.wen.model.entity.RoleEntity;
import com.wen.mapper.UserMapper;
import com.wen.service.UserService;
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

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    /**
     * 创建通过第三方软件登录的用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDto registerUser(String phone) {
        UserDto userDto = queryByPhone(phone);
        // 1. 存在信息直接返回
        if (userDto != null) {
            log.info("手机号注册，该用户存在用户信息: {}", userDto);
            return userDto;
        }
        // 2. 新用户自动注册
        log.info("手机号用户注册：{}", phone);
        long currentTime = System.currentTimeMillis();
        UserEntity userInfo = new UserEntity();
        userInfo.setUserId(UserIdGenerator.generator());
        userInfo.setUsername("phone_" + phone);
        userInfo.setPhone(phone);
        userInfo.setStatus(StatusEnum.NORMAL.getCode());
        userInfo.setDeleted(DeleteEnum.ACTIVE.getCode());
        userInfo.setCreateTime(currentTime);
        userInfo.setUpdateTime(currentTime);
        userMapper.insert(userInfo);
        // 3. 设置用户角色
        RoleEntity userRole = new RoleEntity();
        userRole.setUserId(userInfo.getUserId());
        userRole.setUserName(userInfo.getUsername());
        userRole.setPhone(userInfo.getPhone());
        userRole.setRole(RoleTypeEnum.USER.getCode());
        userRole.setCreateTime(currentTime);
        userRole.setUpdateTime(currentTime);
        roleMapper.insert(userRole);
        log.info("手机号用户注册成功：createUser={}", userInfo);
        // 4. 构建返回类型
        UserDto response = new UserDto();
        BeanUtil.copyProperties(userInfo, response);
        return response;
    }

    @Override
    public List<UserDto> queryByCondition(UserQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StrUtil.isEmpty(request.getUsername()), UserEntity::getUsername, request.getUsername())
                .eq(!StrUtil.isEmpty(request.getPhone()), UserEntity::getPhone, request.getPhone())
                .eq(request.getGender() != null, UserEntity::getGender, request.getGender())
                .eq(!StrUtil.isEmpty(request.getCountry()), UserEntity::getCountry, request.getCountry())
                .eq(!StrUtil.isEmpty(request.getProvince()), UserEntity::getProvince, request.getProvince())
                .eq(!StrUtil.isEmpty(request.getCity()), UserEntity::getCity, request.getCity())
                .eq(request.getStatus() != null, UserEntity::getStatus, request.getStatus())
                .eq(request.getDeleted() != null, UserEntity::getDeleted, request.getDeleted())
                .orderByDesc(UserEntity::getCreateTime);
        // 查询用户列表
        List<UserEntity> userInfoList = userMapper.selectList(wrapper);

        // 转换为 DTO
        List<UserDto> dtoList = new ArrayList<>();
        for (UserEntity userInfo : userInfoList) {
            UserDto dto = new UserDto();
            BeanUtils.copyProperties(userInfo, dto);
            dtoList.add(dto);
        }

        log.info("根据条件查询到的用户信息数量: [{}]", dtoList.size());
        return dtoList;
    }

    @Override
    public UserDto queryByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new BusinessException("输入参数不能为空");
        }
        UserEntity userInfo = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getPhone, phone));
        if (userInfo == null) {
            return null;
        }
        UserDto response = new UserDto();
        BeanUtil.copyProperties(userInfo, response);
        log.info("根据手机号 [{}] 查询用户成功 [{}]", phone, response);
        return response;
    }

    @Override
    public UserEntity queryByUserId(Long userId) {
        UserEntity userInfo = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUserId, userId));
        log.info("根据用户ID [{}] 查询用户成功 [{}]", userId, userInfo);
        return userInfo;
    }

    @Override
    public List<UserEntity> queryByUserIdSet(Set<Long> userIdSet) {
        if (CollectionUtils.isEmpty(userIdSet)) {
            log.info("输入的查询用户ID数量为空");
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserEntity::getUserId, userIdSet);
        List<UserEntity> infoList = userMapper.selectList(wrapper);
        log.info("查询到的用户信息数量: [{}]", infoList.size());
        return infoList;
    }

    @Override
    public void disableUser(Long userId) {
        checkUserExist(userId);
        updateUserStatus(userId, StatusEnum.DISABLED.getCode());
        log.info("禁用用户成功: userId={}", userId);
    }

    @Override
    public void enableUser(Long userId) {
        checkUserExist(userId);
        updateUserStatus(userId, StatusEnum.NORMAL.getCode());
        log.info("启用用户成功: userId={}", userId);
    }

    @Override
    public void deleteUser(Long userId) {
        checkUserExist(userId);
        LambdaUpdateWrapper<UserEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserEntity::getUserId, userId)
                .set(UserEntity::getDeleted, DeleteEnum.DELETED.getCode())
                .set(UserEntity::getUpdateTime, System.currentTimeMillis());
        userMapper.update(null, wrapper);
        log.info("注销用户成功: userId={}", userId);
    }

    /**
     * 更新用户状态
     */
    private void updateUserStatus(Long userId, Integer status) {
        LambdaUpdateWrapper<UserEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserEntity::getUserId, userId)
                .set(UserEntity::getStatus, status)
                .set(UserEntity::getUpdateTime, System.currentTimeMillis());
        userMapper.update(null, wrapper);
    }

    /**
     * 校验用户是否存在
     */
    private void checkUserExist(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (queryByUserId(userId) == null) {
            throw new BusinessException("用户不存在");
        }
    }
}