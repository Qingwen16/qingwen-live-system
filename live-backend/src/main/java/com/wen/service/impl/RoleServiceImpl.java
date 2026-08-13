package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.constant.AuthConstants;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.exception.BusinessException;
import com.wen.model.dto.RoleDto;
import com.wen.mapper.RoleMapper;
import com.wen.model.entity.RoleEntity;
import com.wen.service.RoleService;
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

    private final RoleMapper roleMapper;

    @Override
    public List<RoleDto> queryRole(List<Integer> types) {
        log.info("根据类型 [{}] 查询用户角色信息", types);
        if (CollectionUtils.isEmpty(types)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RoleEntity::getRole, types);
        List<RoleEntity> userRoles = roleMapper.selectList(wrapper);

        List<RoleDto> dtoList = new ArrayList<>();
        for (RoleEntity userRole : userRoles) {
            RoleDto dto = new RoleDto();
            BeanUtil.copyProperties(userRole, dto);
            dtoList.add(dto);
        }

        log.info("根据类型查询用户角色信息数量: [{}]", dtoList.size());
        return dtoList;
    }

    @Override
    public Integer queryRoleByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        RoleEntity userRole = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getUserId, userId));
        return userRole == null ? null : userRole.getRole();
    }

    @Override
    public String setAdmin(String phone, String code) {
        return updateRole(phone, code, RoleTypeEnum.ADMIN);
    }

    @Override
    public String setSuperAdmin(String phone, String code) {
        return updateRole(phone, code, RoleTypeEnum.SUPER_ADMIN);
    }

    @Override
    public String setAnchorRole(String phone) {
        return updateRole(phone, null, RoleTypeEnum.ANCHOR);
    }

    @Override
    public String setUserRole(String phone) {
        return updateRole(phone, null, RoleTypeEnum.USER);
    }

    /**
     * 更新用户角色，code 为空时跳过验证码校验
     */
    private String updateRole(String phone, String code, RoleTypeEnum role) {
        checkPhoneParam(phone);
        if (code != null) {
            if (StrUtil.isEmpty(code)) {
                return "设置角色的验证码为空";
            }
            if (!AuthConstants.ADMIN_CHECK_CODE.equals(code)) {
                return "设置角色的验证码有误";
            }
        }
        if (existUserRole(phone)) {
            return "未查询该用户角色，请检验手机号";
        }
        LambdaUpdateWrapper<RoleEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RoleEntity::getPhone, phone);
        wrapper.set(RoleEntity::getRole, role.getCode());
        wrapper.set(RoleEntity::getUpdateTime, System.currentTimeMillis());
        roleMapper.update(wrapper);
        log.info("用户角色修改成功: [{}] 已被设置为 [{}]", phone, role.getDesc());
        return "用户角色修改成功";
    }

    private boolean existUserRole(String phone) {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getPhone, phone);
        Long count = roleMapper.selectCount(wrapper);
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
