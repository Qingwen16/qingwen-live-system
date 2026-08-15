package com.wen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.common.exception.BusinessException;
import com.wen.common.response.PageResult;
import com.wen.mapper.AddressMapper;
import com.wen.model.entity.AddressEntity;
import com.wen.model.vo.AddressIdRequest;
import com.wen.model.vo.AddressQueryRequest;
import com.wen.model.vo.AddressRequest;
import com.wen.service.AddressService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址服务实现：用户端操作自己的地址，管理端分页查询/强制删除
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressEntity> implements AddressService {

    private static final long MAX_PAGE_SIZE = 100;

    private final AddressMapper addressMapper;

    @Override
    public List<AddressEntity> queryAddress() {
        Long userId = currentUserId();
        LambdaQueryWrapper<AddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressEntity::getUserId, userId)
                .orderByDesc(AddressEntity::getIsDefault)
                .orderByDesc(AddressEntity::getCreateTime);
        return addressMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAddress(AddressRequest request) {
        Long userId = currentUserId();
        // 设置为默认地址时，先取消该用户其他默认地址
        cancelDefaultIfNeeded(userId, request.getIsDefault(), null);

        AddressEntity address = new AddressEntity();
        address.setUserId(userId);
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setCountry(request.getCountry());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setAddress(request.getAddress());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(request.getIsDefault());
        addressMapper.insert(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(AddressRequest request) {
        Long userId = currentUserId();
        // 校验地址归属，防止越权修改他人地址
        checkAddressOwned(request.getId(), userId);
        // 设置为默认地址时，先取消该用户其他默认地址
        cancelDefaultIfNeeded(userId, request.getIsDefault(), request.getId());

        AddressEntity address = new AddressEntity();
        address.setId(request.getId());
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setCountry(request.getCountry());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setAddress(request.getAddress());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(request.getIsDefault());
        addressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(AddressIdRequest request) {
        Long userId = currentUserId();
        LambdaQueryWrapper<AddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressEntity::getId, request.getAddressId())
                .eq(AddressEntity::getUserId, userId);
        addressMapper.delete(wrapper);
    }

    @Override
    public PageResult<AddressEntity> webQueryAddress(AddressQueryRequest request) {
        long pageNum = request.getPageNum() < 1 ? 1 : request.getPageNum();
        long pageSize = request.getPageSize() < 1 ? 10 : request.getPageSize();
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);

        LambdaQueryWrapper<AddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getUserId() != null, AddressEntity::getUserId, request.getUserId())
                .like(StrUtil.isNotBlank(request.getName()), AddressEntity::getName, request.getName())
                .like(StrUtil.isNotBlank(request.getPhone()), AddressEntity::getPhone, request.getPhone())
                .eq(request.getIsDefault() != null, AddressEntity::getIsDefault, request.getIsDefault())
                .orderByDesc(AddressEntity::getCreateTime);

        Page<AddressEntity> page = addressMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void webDeleteAddress(AddressIdRequest request) {
        if (request.getAddressId() == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (addressMapper.selectById(request.getAddressId()) == null) {
            throw new BusinessException("地址不存在");
        }
        addressMapper.deleteById(request.getAddressId());
    }

    /**
     * 获取当前登录用户ID
     */
    private Long currentUserId() {
        Long userId = UserInfoContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        return userId;
    }

    /**
     * 校验地址是否存在且属于当前用户
     */
    private void checkAddressOwned(Long id, Long userId) {
        LambdaQueryWrapper<AddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressEntity::getId, id)
                .eq(AddressEntity::getUserId, userId);
        if (addressMapper.selectCount(wrapper) == 0) {
            throw new BusinessException("地址不存在或无权操作");
        }
    }

    /**
     * 本次操作将地址设为默认时，先取消该用户其余默认地址
     *
     * @param userId     用户ID
     * @param isDefault  本次是否设为默认
     * @param excludeId  更新时需排除的地址ID，新增时传 null
     */
    private void cancelDefaultIfNeeded(Long userId, Integer isDefault, Long excludeId) {
        if (isDefault == null || isDefault != 1) {
            return;
        }
        LambdaUpdateWrapper<AddressEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressEntity::getUserId, userId)
                .eq(AddressEntity::getIsDefault, 1);
        if (excludeId != null) {
            wrapper.ne(AddressEntity::getId, excludeId);
        }
        wrapper.set(AddressEntity::getIsDefault, 0);
        addressMapper.update(null, wrapper);
    }
}
