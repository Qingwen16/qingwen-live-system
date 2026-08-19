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
    Long createGood(GoodAddRequest request);

    /**
     * 修改商品
     */
    void updateGood(GoodUdtRequest request);

    /**
     * 删除商品（软删除）
     */
    void deleteGood(GoodIdRequest request);

    /**
     * 查询商品列表（全量，支持筛选）
     */
    List<GoodDto> queryGoods(GoodGetRequest request);

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

    /**
     * 挂载商品到当前主播直播间（一个直播间最多 3 个商品）
     */
    void mountToRoom(GoodIdRequest request);

    /**
     * 从当前主播直播间移除商品
     */
    void unmountFromRoom(GoodIdRequest request);

}
