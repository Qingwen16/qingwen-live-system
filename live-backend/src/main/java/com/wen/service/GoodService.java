package com.wen.service;

import com.wen.model.dto.GoodDto;
import com.wen.model.vo.*;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface GoodService {

    /**
     * 新增商品（默认未上架）
     */
    Long createGood(GoodCreateRequest request);

    /**
     * 修改商品
     */
    void updateGood(GoodUpdateRequest request);

    /**
     * 删除商品（软删除）
     */
    void deleteGood(GoodIdRequest request);

    /**
     * 查询商品列表（全量，支持筛选）
     */
    List<GoodDto> queryGoods(GoodQueryRequest request);

    /**
     * 查询商品列表（全量，支持筛选）
     */
    List<GoodDto> queryAppGoods();

    /**
     * 查询直播间已上架且库存充足的商品（用户端）
     */
    List<GoodDto> queryRoomGoods(RoomIdRequest request);

    /**
     * 上架商品
     */
    void onShelf(GoodIdRequest goodId);

    /**
     * 下架商品
     */
    void offShelf(GoodIdRequest goodId);

}
