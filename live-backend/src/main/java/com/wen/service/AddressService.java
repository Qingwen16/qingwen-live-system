package com.wen.service;

import com.wen.model.entity.AddressEntity;
import com.wen.model.vo.AddressRequest;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
public interface AddressService {

    /**
     * 查询当前登录用户的收货地址
     */
    List<AddressEntity> queryUserAddress();

    /**
     * 新增收货地址
     */
    void createUserAddress(AddressRequest request);

    /**
     * 修改收货地址
     */
    void updateUserAddress(AddressRequest request);

    /**
     * 删除收货地址
     */
    void deleteUserAddress(Long id);
}
