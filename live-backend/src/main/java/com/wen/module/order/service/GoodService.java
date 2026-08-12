package com.wen.module.order.service;

import com.wen.module.order.domain.vo.GoodCreateRequest;
import com.wen.module.order.domain.vo.GoodInfoDto;
import com.wen.module.order.domain.vo.GoodUpdateRequest;
import com.wen.module.order.domain.entity.GoodInfo;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface GoodService {

    /**
     * 新增商品
     */
    String createGood(GoodCreateRequest request);

    /**
     * 修改商品
     */
    String updateGood(GoodUpdateRequest request);

    /**
     * 删除商品
     */
    String deleteGood(Long goodId);

    /**
     * 获取所有商品
     */
    GoodInfo queryGoodById(Long goodId);

    /**
     * 获取所有商品
     */
    List<GoodInfoDto> queryTotalGoods();

    /**
     * 获取所有上架商品列表
     */
    List<GoodInfoDto> queryTotalListedGoods();

    /**
     * 扣减库存
     */
    boolean reduceGoodStock(Long goodId, Integer quantity);
}
