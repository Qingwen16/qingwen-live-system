package com.wen.module.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.constant.AuthConstants;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.exception.BusinessException;
import com.wen.module.user.domain.vo.UserRoleDto;
import com.wen.module.user.mapper.UserRoleMapper;
import com.wen.module.user.domain.entity.UserRole;
import com.wen.module.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRoleMapper userRoleMapper;

    @Override
    public List<UserRoleDto> queryRole(List<Integer> types) {
        log.info("根据类型 [{}] 查询用户角色信息", types);
        if (CollectionUtils.isEmpty(types)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserRole::getRole, types);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);

        List<UserRoleDto> dtoList = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            UserRoleDto dto = new UserRoleDto();
            BeanUtil.copyProperties(userRole, dto);
            dtoList.add(dto);
        }

        log.info("根据类型查询用户角色信息数量: [{}]", dtoList.size());
        return dtoList;
    }

    @Override
    public String setAdmin(String phone, String code) {
        checkPhoneParam(phone);
        if (StrUtil.isEmpty(code)) {
            return "设置管理员的验证码为空";
        }
        if (!AuthConstants.ADMIN_CHECK_CODE.equals(code)) {
            return "设置管理员的验证码有误";
        }
        if (existUserRole(phone)) {
            return "未查询该用户角色，请检验手机号";
        }
        LambdaUpdateWrapper<UserRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserRole::getPhone, phone);
        wrapper.set(UserRole::getRole, RoleTypeEnum.ADMIN.getCode());
        wrapper.set(UserRole::getUpdateTime, System.currentTimeMillis());
        userRoleMapper.update(wrapper);
        log.info("用户据色修改成功: [{}] 已被设置为管理员", phone);
        return "用户角色修改成功";
    }

    @Override
    public String setRoomAdminRole(String phone) {
        checkPhoneParam(phone);
        if (existUserRole(phone)) {
            return "未查询该用户角色，请检验手机号";
        }
        LambdaUpdateWrapper<UserRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserRole::getPhone, phone);
        wrapper.set(UserRole::getRole, RoleTypeEnum.ROOM.getCode());
        wrapper.set(UserRole::getUpdateTime, System.currentTimeMillis());
        userRoleMapper.update(wrapper);
        log.info("用户角色修改成功: [{}] 已被设置为房管", phone);
        return "用户角色修改成功";
    }

    @Override
    public String setAnchorRole(String phone) {
        checkPhoneParam(phone);
        if (existUserRole(phone)) {
            return "未查询该用户角色，请检验手机号";
        }
        LambdaUpdateWrapper<UserRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserRole::getPhone, phone);
        wrapper.set(UserRole::getRole, RoleTypeEnum.ANCHOR.getCode());
        wrapper.set(UserRole::getUpdateTime, System.currentTimeMillis());
        userRoleMapper.update(wrapper);
        log.info("用户角色修改成功: [{}] 已被设置为主播", phone);
        return "用户角色修改成功";
    }

    @Override
    public String setUserRole(String phone) {
        checkPhoneParam(phone);
        if (existUserRole(phone)) {
            return "未查询该用户角色，请检验手机号";
        }
        LambdaUpdateWrapper<UserRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserRole::getPhone, phone);
        wrapper.set(UserRole::getRole, RoleTypeEnum.USER.getCode());
        wrapper.set(UserRole::getUpdateTime, System.currentTimeMillis());
        userRoleMapper.update(wrapper);
        log.info("用户角色修改成功: [{}] 已被设置为普通用户", phone);
        return "用户角色修改成功";
    }

    private boolean existUserRole(String phone) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getPhone, phone);
        Long count = userRoleMapper.selectCount(wrapper);
        return count == null || count <= 0;
    }

    private void checkPhoneParam(String phone) {
        if (StrUtil.isEmpty(phone)) {
            throw new BusinessException("输入参数不能为空");
        }
        if (!phone.matches(AuthConstants.PHONE_REGEX)) {
            throw new BusinessException("手机号格式不正确");
        }
    }

}
