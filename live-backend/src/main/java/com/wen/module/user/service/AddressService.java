package com.wen.module.user.service;

import com.wen.module.user.domain.vo.AddressRequest;
import com.wen.module.user.domain.entity.UserAddress;
import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
public interface AddressService {
    /**
     * 查询用户收货地址
     */
    List<UserAddress> queryUserAddress(Long userId);
    /**
     * 新增用户收货地址
     */
    void createUserAddress(AddressRequest request);
    /**
     * 修改用户收货地址
     */
    void updateUserAddress(AddressRequest request);
    /**
     * 删除用户收货地址
     */
    void deleteUserAddress(Long id, Long userId);
}