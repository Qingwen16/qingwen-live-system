package com.wen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.model.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天消息 Mapper
 *
 * @author : rjw
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {
}
