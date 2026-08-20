package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.exception.BusinessException;
import com.wen.common.response.PageResult;
import com.wen.mapper.RoleMapper;
import com.wen.model.dto.RoleDto;
import com.wen.model.entity.RoleEntity;
import com.wen.model.vo.RoleGetRequest;
import com.wen.service.CacheService;
import com.wen.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    private final CacheService cacheService;

    @Override
    public PageResult<RoleDto> queryRole(RoleGetRequest request) {
        List<Integer> types = request.getTypes();
        log.info("分页查询用户角色信息, types={}, pageNum={}, pageSize={}", types,
                request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getDeleted, DeleteEnum.ACTIVE.getCode());
        if (!CollectionUtils.isEmpty(types)) {
            wrapper.in(RoleEntity::getRole, types);
        }

        Page<RoleEntity> page = roleMapper.selectPage(
                new Page<>(request.getPageNum(), request.getPageSize()), wrapper);

        List<RoleDto> dtoList = new ArrayList<>();
        for (RoleEntity userRole : page.getRecords()) {
            RoleDto dto = new RoleDto();
            BeanUtil.copyProperties(userRole, dto);
            dtoList.add(dto);
        }

        log.info("分页查询用户角色数量: [{}], 总数: [{}]", dtoList.size(), page.getTotal());
        return PageResult.of(dtoList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public Integer queryRoleByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        // 先查缓存，命中直接返回
        Integer cached = cacheService.getUserRoleCache(userId);
        if (cached != null) {
            return cached;
        }
        Integer role = queryRoleByUserIdFromDb(userId);
        if (role == null) {
            setRole(userId, RoleTypeEnum.USER.getCode());
        }
        cacheService.setUserRoleCache(userId, role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setRole(Long userId, Integer role) {
        RoleTypeEnum roleType = RoleTypeEnum.of(role);
        if (roleType == null) {
            throw new BusinessException("非法的角色类型: " + role);
        }
        // 存在则更新（含恢复软删除记录），不存在则插入，避免 userId 唯一索引冲突
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getUserId, userId);
        RoleEntity existing = roleMapper.selectOne(wrapper);
        if (existing == null) {
            RoleEntity entity = new RoleEntity();
            entity.setUserId(userId);
            entity.setRole(roleType.getCode());
            entity.setDeleted(DeleteEnum.ACTIVE.getCode());
            roleMapper.insert(entity);
        } else {
            LambdaUpdateWrapper<RoleEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(RoleEntity::getUserId, userId)
                    .set(RoleEntity::getRole, roleType.getCode())
                    .set(RoleEntity::getDeleted, DeleteEnum.ACTIVE.getCode());
            roleMapper.update(null, updateWrapper);
        }
        // 角色变更后失效缓存，保证拦截器实时读取到新角色
        cacheService.delUserRoleCache(userId);
        log.info("用户角色修改成功: userId={}, role={}", userId, roleType.getDesc());
    }

    private Integer queryRoleByUserIdFromDb(Long userId) {
        RoleEntity userRole = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getUserId, userId)
                .eq(RoleEntity::getDeleted, DeleteEnum.ACTIVE.getCode()));
        return userRole == null ? null : userRole.getRole();
    }

}
