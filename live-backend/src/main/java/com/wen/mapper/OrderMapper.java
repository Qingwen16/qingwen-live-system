package com.wen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.model.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}
