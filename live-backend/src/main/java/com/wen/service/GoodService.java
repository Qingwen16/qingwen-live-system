package com.wen.service;

import com.wen.model.vo.GoodCreateRequest;
import com.wen.model.dto.GoodDto;
import com.wen.model.vo.GoodUpdateRequest;
import com.wen.model.entity.GoodEntity;

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
    GoodEntity queryGoodById(Long goodId);

    /**
     * 获取所有商品
     */
    List<GoodDto> queryTotalGoods();

    /**
     * 获取所有上架商品列表
     */
    List<GoodDto> queryTotalListedGoods();

    /**
     * 扣减库存
     */
    boolean reduceGoodStock(Long goodId, Integer quantity);
}
