package com.wen.service;

import com.wen.common.response.PageResult;
import com.wen.model.entity.AddressEntity;
import com.wen.model.vo.AddressQueryRequest;

/**
 * 后台地址管理服务（Web 管理端）
 *
 * @author : rjw
 * @date : 2026-04-09
 */
public interface AddressWebService {

    /**
     * 分页查询地址
     */
    PageResult<AddressEntity> pageQuery(AddressQueryRequest request);

    /**
     * 删除地址（管理端强制删除）
     */
    void deleteAddress(Long id);
}
