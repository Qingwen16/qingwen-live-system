package com.wen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.model.entity.AnchorRoomRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 主播-直播间关联表 Mapper
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@Mapper
public interface AnchorRoomRelationMapper extends BaseMapper<AnchorRoomRelation> {
}
