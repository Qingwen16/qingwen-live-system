package com.wen.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.module.user.domain.vo.AddressRequest;
import com.wen.module.user.domain.entity.UserAddress;
import com.wen.module.user.mapper.UserAddressMapper;
import com.wen.module.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements AddressService {
    private final UserAddressMapper userAddressMapper;
    @Override
    public List<UserAddress> queryUserAddress(Long userId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime);
        return userAddressMapper.selectList(wrapper);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUserAddress(AddressRequest request) {
        // 如果设置为默认地址，先取消该用户其他默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserAddress::getUserId, request.getUserId())
                    .set(UserAddress::getIsDefault, 0);
            userAddressMapper.update(null, updateWrapper);
        }
        UserAddress address = new UserAddress();
        address.setUserId(request.getUserId());
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setCountry(request.getCountry());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setAddress(request.getAddress());
        address.setPostalCode(request.getPostalCode());
        address.setZipCode(request.getZipCode());
        address.setIsDefault(request.getIsDefault());
        // 拼接完整地址
        String fullAddress = (request.getProvince() != null ? request.getProvince() : "") +
                (request.getCity() != null ? request.getCity() : "") +
                (request.getAddress() != null ? request.getAddress() : "");
        address.setFullAddress(fullAddress);
        address.setCreateTime(System.currentTimeMillis());
        address.setUpdateTime(System.currentTimeMillis());
        userAddressMapper.insert(address);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserAddress(AddressRequest request) {
        // 如果设置为默认地址，先取消该用户其他默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserAddress::getUserId, request.getUserId())
                    .ne(UserAddress::getId, request.getId())
                    .set(UserAddress::getIsDefault, 0);
            userAddressMapper.update(null, updateWrapper);
        }
        UserAddress address = new UserAddress();
        address.setId(request.getId());
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setCountry(request.getCountry());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setAddress(request.getAddress());
        address.setPostalCode(request.getPostalCode());
        address.setZipCode(request.getZipCode());
        address.setIsDefault(request.getIsDefault());
        // 拼接完整地址
        String fullAddress = (request.getProvince() != null ? request.getProvince() : "") +
                (request.getCity() != null ? request.getCity() : "") +
                (request.getAddress() != null ? request.getAddress() : "");
        address.setFullAddress(fullAddress);
        address.setUpdateTime(System.currentTimeMillis());
        userAddressMapper.updateById(address);
    }
    @Override
    public void deleteUserAddress(Long id, Long userId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getId, id)
                .eq(UserAddress::getUserId, userId);
        userAddressMapper.delete(wrapper);
    }
}