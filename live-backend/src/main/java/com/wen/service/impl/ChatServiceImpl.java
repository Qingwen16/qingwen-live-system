package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.exception.BusinessException;
import com.wen.mapper.ChatMessageMapper;
import com.wen.model.entity.ChatMessageEntity;
import com.wen.model.entity.UserEntity;
import com.wen.model.dto.ChatMessageDto;
import com.wen.model.vo.ChatSendRequest;
import com.wen.service.ChatService;
import com.wen.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播间聊天服务实现
 *
 * @author : rjw
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final int DEFAULT_HISTORY_SIZE = 50;
    private static final int MAX_HISTORY_SIZE = 200;

    private final ChatMessageMapper chatMessageMapper;
    private final UserService userService;

    @Override
    public ChatMessageDto sendMessage(Long userId, ChatSendRequest request) {
        if (request == null || request.getRoomId() == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        if (StrUtil.isBlank(request.getContent())) {
            throw new BusinessException("消息内容不能为空");
        }
        if (userId == null) {
            throw new BusinessException("未登录或登录已失效");
        }
        UserEntity user = userService.queryByUserId(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        long currentTime = System.currentTimeMillis();
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setRoomId(request.getRoomId());
        entity.setUserId(userId);
        entity.setUsername(user.getUsername());
        entity.setContent(request.getContent());
        entity.setCreateTime(currentTime);
        entity.setUpdateTime(currentTime);
        entity.setDeleted(DeleteEnum.ACTIVE.getCode());
        chatMessageMapper.insert(entity);

        log.info("直播间 [{}] 用户 [{}] 发送消息成功", request.getRoomId(), userId);
        return toMessage(entity);
    }

    @Override
    public List<ChatMessageDto> queryHistory(Long roomId, Integer limit) {
        if (roomId == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        int size = (limit == null || limit < 1) ? DEFAULT_HISTORY_SIZE : Math.min(limit, MAX_HISTORY_SIZE);

        List<ChatMessageEntity> entities = chatMessageMapper.selectList(new LambdaQueryWrapper<ChatMessageEntity>()
                .eq(ChatMessageEntity::getRoomId, roomId)
                .eq(ChatMessageEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .orderByDesc(ChatMessageEntity::getCreateTime)
                .last("LIMIT " + size));

        // 倒序查出，转正序返回给前端
        List<ChatMessageDto> messages = new ArrayList<>(entities.size());
        for (int i = entities.size() - 1; i >= 0; i--) {
            messages.add(toMessage(entities.get(i)));
        }
        return messages;
    }

    private ChatMessageDto toMessage(ChatMessageEntity entity) {
        ChatMessageDto message = new ChatMessageDto();
        BeanUtil.copyProperties(entity, message);
        return message;
    }

}
