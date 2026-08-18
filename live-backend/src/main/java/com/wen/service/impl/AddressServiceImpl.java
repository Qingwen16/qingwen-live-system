package com.wen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.common.enums.DefaultEnum;
import com.wen.common.exception.BusinessException;
import com.wen.common.response.PageResult;
import com.wen.mapper.AddressMapper;
import com.wen.model.entity.AddressEntity;
import com.wen.model.vo.AddressIdRequest;
import com.wen.model.vo.AddressQueryRequest;
import com.wen.model.vo.AddressInsertRequest;
import com.wen.service.AddressService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private static final long MAX_PAGE_SIZE = 100;

    private final AddressMapper addressMapper;

    @Override
    public List<AddressEntity> queryAddress() {
        Long userId = UserInfoContext.getUserId();
        LambdaQueryWrapper<AddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressEntity::getUserId, userId)
                .orderByDesc(AddressEntity::getIsDefault)
                .orderByDesc(AddressEntity::getCreateTime);
        return addressMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAddress(AddressInsertRequest request) {
        Long userId = UserInfoContext.getUserId();
        // 设置为默认地址时，先取消该用户其他默认地址
        cancelDefaultAddress(userId, request.getIsDefault());
        // 创建地址实体类
        AddressEntity address = new AddressEntity();
        address.setUserId(userId);
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setAddress(request.getAddress());
        address.setIsDefault(request.getIsDefault());
        addressMapper.insert(address);
        log.info("用户创建地址成功 - 地址 - [{}]", address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(AddressInsertRequest request) {
        Long userId = UserInfoContext.getUserId();
        // 校验地址归属，防止越权修改他人地址
        AddressEntity address = addressMapper.selectById(request.getId());
        if (address == null) {
            throw new BusinessException("所要修改的地址不存在");
        }
        if (!userId.equals(address.getUserId())) {
            throw new BusinessException("所要修改的地址不是本人地址");
        }
        // 设置为默认地址时，先取消该用户其他默认地址
        cancelDefaultAddress(userId, request.getIsDefault());
        // 组装/更新字段
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setAddress(request.getAddress());
        address.setIsDefault(request.getIsDefault());
        addressMapper.updateById(address);
        log.info("用户修改地址成功 - 地址 - [{}]", address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(AddressIdRequest request) {
        Long userId = UserInfoContext.getUserId();
        LambdaQueryWrapper<AddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressEntity::getId, request.getAddressId())
                .eq(AddressEntity::getUserId, userId);
        addressMapper.delete(wrapper);
        log.info("用户删除地址成功 - 地址ID - [{}], 用户ID - [{}]", request, userId);
    }

    @Override
    public PageResult<AddressEntity> webQueryAddress(AddressQueryRequest request) {
        long pageNum = request.getPageNum() < 1 ? 1 : request.getPageNum();
        long pageSize = request.getPageSize() < 1 ? 10 : request.getPageSize();
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);

        LambdaQueryWrapper<AddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getUserId() != null,
                        AddressEntity::getUserId, request.getUserId())
                .like(StrUtil.isNotBlank(request.getName()),
                        AddressEntity::getName, request.getName())
                .like(StrUtil.isNotBlank(request.getPhone()),
                        AddressEntity::getPhone, request.getPhone())
                .eq(request.getIsDefault() != null,
                        AddressEntity::getIsDefault, request.getIsDefault())
                .orderByDesc(AddressEntity::getCreateTime);

        Page<AddressEntity> page = addressMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void webDeleteAddress(AddressIdRequest request) {
        if (addressMapper.selectById(request.getAddressId()) == null) {
            throw new BusinessException("地址不存在");
        }
        addressMapper.deleteById(request.getAddressId());
        log.info("管理员删除地址成功 - 地址ID - [{}]", request);
    }

    /**
     * 本次操作将地址设为默认时，先取消该用户其余默认地址
     */
    private void cancelDefaultAddress(Long userId, Integer isDefault) {
        // 添加安全判断，如果不使默认地址，就不能取消已存在默认地址
        if (isDefault == null || isDefault != DefaultEnum.YES.getCode()) {
            return;
        }
        LambdaUpdateWrapper<AddressEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressEntity::getUserId, userId)
                .eq(AddressEntity::getIsDefault, DefaultEnum.YES.getCode())
                .set(AddressEntity::getIsDefault, DefaultEnum.NO.getCode());
        addressMapper.update(null, wrapper);
    }
}
