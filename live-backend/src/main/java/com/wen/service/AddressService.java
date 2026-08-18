package com.wen.service;

import com.wen.common.response.PageResult;
import com.wen.model.entity.AddressEntity;
import com.wen.model.vo.AddressIdRequest;
import com.wen.model.vo.AddressQueryRequest;
import com.wen.model.vo.AddressInsertRequest;

import java.util.List;

/**
 * 地址服务：用户端操作自己的地址，管理端分页查询/强制删除
 *
 * @author : rjw
 * @date : 2026-04-09
 */
public interface AddressService {

    /**
     * 查询当前登录用户的收货地址
     */
    List<AddressEntity> queryAddress();

    /**
     * 新增收货地址
     */
    void createAddress(AddressInsertRequest request);

    /**
     * 修改收货地址
     */
    void updateAddress(AddressInsertRequest request);

    /**
     * 删除自己的收货地址
     */
    void deleteAddress(AddressIdRequest request);

    /**
     * 分页查询地址（管理端）
     */
    PageResult<AddressEntity> webQueryAddress(AddressQueryRequest request);

    /**
     * 强制删除地址（管理端）
     */
    void webDeleteAddress(AddressIdRequest request);
}
