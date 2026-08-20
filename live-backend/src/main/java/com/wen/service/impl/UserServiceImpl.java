package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.enums.StatusEnum;
import com.wen.common.exception.BusinessException;
import com.wen.common.response.PageResult;
import com.wen.model.dto.UserDto;
import com.wen.mapper.RoleMapper;
import com.wen.model.vo.PhoneRequest;
import com.wen.model.vo.UserIdRequest;
import com.wen.model.vo.UserGetRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        userInfo.setUsername("phone_" + phone);
        userInfo.setPhone(phone);
        userInfo.setStatus(StatusEnum.NORMAL.getCode());
        userInfo.setDeleted(DeleteEnum.ACTIVE.getCode());
        userInfo.setCreateTime(currentTime);
        userInfo.setUpdateTime(currentTime);
        userMapper.insert(userInfo);
        // 3. 设置用户角色（userId 由雪花算法在插入时回填）
        RoleEntity userRole = new RoleEntity();
        userRole.setUserId(userInfo.getUserId());
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
    public PageResult<UserDto> queryByCondition(UserGetRequest request) {

        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StrUtil.isEmpty(request.getUsername()),
                        UserEntity::getUsername, request.getUsername())
                .eq(!StrUtil.isEmpty(request.getPhone()),
                        UserEntity::getPhone, request.getPhone())
                .eq(request.getGender() != null,
                        UserEntity::getGender, request.getGender())
                .eq(request.getStatus() != null,
                        UserEntity::getStatus, request.getStatus())
                .eq(request.getDeleted() != null,
                        UserEntity::getDeleted, request.getDeleted())
                .ge(request.getCreateTimeStart() != null,
                        UserEntity::getCreateTime, request.getCreateTimeStart())
                .le(request.getCreateTimeEnd() != null,
                        UserEntity::getCreateTime, request.getCreateTimeEnd())
                .orderByDesc(UserEntity::getCreateTime);

        // 角色过滤：角色在独立表，先查满足角色的 userId 集合再 IN
        Integer role = request.getRole();
        if (role != null) {
            List<RoleEntity> roles = roleMapper.selectList(new LambdaQueryWrapper<RoleEntity>()
                    .eq(RoleEntity::getRole, role));
            Set<Long> roleUserIdSet = roles.stream()
                    .map(RoleEntity::getUserId)
                    .collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(roleUserIdSet)) {
                return PageResult.of(Collections.emptyList(), 0L, request.getPageNum(), request.getPageSize());
            }
            wrapper.in(UserEntity::getUserId, roleUserIdSet);
        }

        Page<UserEntity> page = userMapper.selectPage(
                new Page<>(request.getPageNum(), request.getPageSize()), wrapper);

        List<UserDto> dtoList = new ArrayList<>();
        for (UserEntity userInfo : page.getRecords()) {
            UserDto dto = new UserDto();
            BeanUtils.copyProperties(userInfo, dto);
            dtoList.add(dto);
        }
        fillRoles(dtoList);

        log.info("根据条件分页查询用户数量: [{}], 总数: [{}]", dtoList.size(), page.getTotal());
        return PageResult.of(dtoList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public UserDto queryByPhone(String phone) {
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
    public void disableUser(UserIdRequest request) {
        Long userId = request.getUserId();
        if (queryByUserId(userId) == null) {
            throw new BusinessException("用户不存在");
        }
        updateUserStatus(userId, StatusEnum.DISABLED.getCode());
        log.info("禁用用户成功: userId={}", userId);
    }

    @Override
    public void enableUser(UserIdRequest request) {
        Long userId = request.getUserId();
        if (queryByUserId(userId) == null) {
            throw new BusinessException("用户不存在");
        }
        updateUserStatus(userId, StatusEnum.NORMAL.getCode());
        log.info("启用用户成功: userId={}", userId);
    }

    @Override
    public void deleteUser(UserIdRequest request) {
        Long userId = request.getUserId();
        if (queryByUserId(userId) == null) {
            throw new BusinessException("用户不存在");
        }
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
     * 批量回填用户角色，避免逐条查询角色表
     */
    private void fillRoles(List<UserDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        Set<Long> userIdSet = dtoList.stream()
                .map(UserDto::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIdSet)) {
            return;
        }
        List<RoleEntity> roles = roleMapper.selectList(new LambdaQueryWrapper<RoleEntity>()
                .in(RoleEntity::getUserId, userIdSet));
        Map<Long, Integer> roleMap = new HashMap<>();
        for (RoleEntity role : roles) {
            roleMap.put(role.getUserId(), role.getRole());
        }
        for (UserDto dto : dtoList) {
            dto.setRole(roleMap.get(dto.getUserId()));
        }
    }
}