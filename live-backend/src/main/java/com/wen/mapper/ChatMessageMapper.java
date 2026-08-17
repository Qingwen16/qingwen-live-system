package com.wen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.model.entity.ChatMessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天消息 Mapper
 *
 * @author : rjw
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessageEntity> {
}
