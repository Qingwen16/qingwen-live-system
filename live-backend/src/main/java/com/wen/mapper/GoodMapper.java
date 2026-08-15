package com.wen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.model.entity.GoodEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Mapper
public interface GoodMapper extends BaseMapper<GoodEntity> {

    /**
     * 原子扣减库存，防止超卖（status=1 已上架，deleted=1 未删除）
     * 影响行数为 0 表示库存不足或商品已下架
     */
    @Update("UPDATE good_entity SET stock_count = stock_count - #{quantity}, " +
            "sales_count = sales_count + #{quantity}, update_time = #{updateTime} " +
            "WHERE id = #{goodId} AND status = 1 AND stock_count >= #{quantity} AND deleted = 1")
    int reduceStock(@Param("goodId") Long goodId, @Param("quantity") Integer quantity,
                    @Param("updateTime") Long updateTime);

    /**
     * 库存扣到 0 后，将已上架商品置为缺货
     */
    @Update("UPDATE good_entity SET status = 2, update_time = #{updateTime} " +
            "WHERE id = #{goodId} AND stock_count <= 0 AND status = 1")
    int markOutOfStockIfEmpty(@Param("goodId") Long goodId, @Param("updateTime") Long updateTime);
}
