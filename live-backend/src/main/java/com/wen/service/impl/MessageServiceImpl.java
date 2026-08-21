package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.exception.BusinessException;
import com.wen.common.exception.ForbiddenException;
import com.wen.common.response.PageResult;
import com.wen.mapper.MessageMapper;
import com.wen.mapper.UserRoomMapper;
import com.wen.model.entity.MessageEntity;
import com.wen.model.entity.UserEntity;
import com.wen.model.entity.UserRoom;
import com.wen.model.dto.MessageDto;
import com.wen.model.vo.MessageQueryWebRequest;
import com.wen.model.vo.MessageIdRequest;
import com.wen.model.vo.MessageSendRequest;
import com.wen.service.MessageService;
import com.wen.service.UserService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 直播间聊天服务实现
 *
 * @author : rjw
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private static final long MAX_PAGE_SIZE = 100;

    private final MessageMapper messageMapper;

    private final UserRoomMapper userRoomMapper;

    private final UserService userService;

    @Override
    public MessageDto sendMessage(Long userId, MessageSendRequest request) {
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
        MessageEntity entity = new MessageEntity();
        entity.setRoomId(request.getRoomId());
        entity.setUserId(userId);
        entity.setUsername(user.getUsername());
        entity.setContent(request.getContent());
        entity.setCreateTime(currentTime);
        entity.setUpdateTime(currentTime);
        entity.setDeleted(DeleteEnum.ACTIVE.getCode());
        messageMapper.insert(entity);

        log.info("直播间 [{}] 用户 [{}] 发送消息成功", request.getRoomId(), userId);
        return toMessage(entity);
    }

    @Override
    public List<MessageDto> getRoomMessage(Long roomId, Integer limit) {
        LambdaQueryWrapper<MessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageEntity::getRoomId, roomId)
                .eq(MessageEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .orderByDesc(MessageEntity::getCreateTime)
                .last("LIMIT " + limit);
        List<MessageEntity> entities = messageMapper.selectList(wrapper);

        // 倒序查出，转正序返回给前端
        List<MessageDto> messages = new ArrayList<>(entities.size());
        for (int i = entities.size() - 1; i >= 0; i--) {
            messages.add(toMessage(entities.get(i)));
        }
        return messages;
    }

    @Override
    public PageResult<MessageDto> getWebMessage(MessageQueryWebRequest request) {
        LambdaQueryWrapper<MessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getRoomId() != null, MessageEntity::getRoomId, request.getRoomId())
                .eq(request.getDeleted() != null, MessageEntity::getDeleted, request.getDeleted())
                .orderByDesc(MessageEntity::getCreateTime);
        Page<MessageEntity> page = messageMapper.selectPage(
                new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        List<MessageDto> records = page.getRecords().stream()
                .map(this::toMessage)
                .toList();
        return PageResult.of(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long deleteMessage(MessageIdRequest request) {
        Long messageId = request.getMessageId();
        if (messageId == null) {
            throw new BusinessException("消息ID不能为空");
        }
        MessageEntity entity = messageMapper.selectById(messageId);
        if (entity == null || Objects.equals(entity.getDeleted(), DeleteEnum.DELETED.getCode())) {
            throw new BusinessException("消息不存在或已删除");
        }

        Integer role = UserInfoContext.getRole();
        Long operatorId = UserInfoContext.getUserId();
        // 主播仅能删除自己直播间的弹幕；管理员/超管可删除任意直播间
        if (role != null && role == RoleTypeEnum.ANCHOR.getCode()) {
            Long count = userRoomMapper.selectCount(new LambdaQueryWrapper<UserRoom>()
                    .eq(UserRoom::getUserId, operatorId)
                    .eq(UserRoom::getRoomId, entity.getRoomId())
                    .eq(UserRoom::getDeleted, DeleteEnum.ACTIVE.getCode()));
            if (count == null || count == 0) {
                throw new ForbiddenException("无权删除该直播间的消息");
            }
        }

        messageMapper.update(null, new LambdaUpdateWrapper<MessageEntity>()
                .eq(MessageEntity::getId, messageId)
                .set(MessageEntity::getDeleted, DeleteEnum.DELETED.getCode())
                .set(MessageEntity::getUpdateTime, System.currentTimeMillis()));
        log.info("消息 [{}] 已被用户 [{}] 从直播间 [{}] 删除", messageId, operatorId, entity.getRoomId());
        return entity.getRoomId();
    }

    private MessageDto toMessage(MessageEntity entity) {
        MessageDto message = new MessageDto();
        BeanUtil.copyProperties(entity, message);
        return message;
    }

}
