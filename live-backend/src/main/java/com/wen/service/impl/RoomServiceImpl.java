package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.enums.DefaultEnum;
import com.wen.common.generator.StreamNameGenerator;
import com.wen.config.LiveStreamConfig;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.RoomStatusEnum;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.exception.BusinessException;
import com.wen.common.generator.RoomIdGenerator;
import com.wen.mapper.UserRoomMapper;
import com.wen.mapper.RoomMapper;
import com.wen.model.entity.UserRoom;
import com.wen.model.entity.RoomEntity;
import com.wen.model.dto.RoomDto;
import com.wen.model.vo.*;
import com.wen.service.RoleService;
import com.wen.service.RoomService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

/**
 * 直播间服务实现类
 *
 * @author jwruan
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;

    private final UserRoomMapper userRoomMapper;

    private final RoomIdGenerator roomIdGenerator;

    private final LiveStreamConfig liveStreamConfig;

    private final StreamNameGenerator streamNameGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRoom(RoomCreateRequest request) {
        // 校验用户角色是不是主播
        Long roomId = request.getRoomId();
        if (request.getIsRandom() == DefaultEnum.YES.getCode()) {
            roomId = roomIdGenerator.generate();
        }
        RoomEntity room = new RoomEntity();
        room.setRoomId(roomId);
        room.setStreamName(streamNameGenerator.generateStreamName());
        room.setTitle(request.getTitle());
        room.setTotalLiveHours(0L);
        room.setTotalIncome(new BigDecimal("0.00"));
        room.setStatus(RoomStatusEnum.NOT_STARTED.getCode());
        roomMapper.insert(room);

        UserRoom userRoom = new UserRoom();
        userRoom.setUserId(request.getUserId());
        userRoom.setRoomId(roomId);
        userRoomMapper.insert(userRoom);

        log.info("创建直播间成功: [{}]", request);
        log.info("创建直播间成功: streamName: [{}]", room.getStreamName());
    }

    @Override
    public void updateRoom(RoomUpdateRequest request) {
        // 判断是不是直播间的主播
        checkIsOwnerRoom(request.getRoomId());

        LambdaUpdateWrapper<RoomEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RoomEntity::getRoomId, request.getRoomId());
        if (StrUtil.isNotEmpty(request.getTitle())) {
            wrapper.set(RoomEntity::getTitle, request.getTitle());
        }
        wrapper.set(RoomEntity::getUpdateTime, System.currentTimeMillis());
        roomMapper.update(null, wrapper);

        log.info("直播间 [{}] 更新成功", request.getRoomId());
        RoomIdRequest roomIdRequest = new RoomIdRequest();
        roomIdRequest.setRoomId(request.getRoomId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(RoomIdRequest request) {
        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getRoomId, request.getRoomId())
                .set(RoomEntity::getDeleted, DeleteEnum.DELETED.getCode()));
        // 直播间删除时同步逻辑删除关联关系，避免残留无效关联
        userRoomMapper.update(null, new LambdaUpdateWrapper<UserRoom>()
                .eq(UserRoom::getRoomId, request.getRoomId())
                .eq(UserRoom::getDeleted, DeleteEnum.ACTIVE.getCode())
                .set(UserRoom::getDeleted, DeleteEnum.DELETED.getCode()));
        log.info("直播间 [{}] 已删除", request.getRoomId());
    }

    @Override
    public void openRoom(RoomIdRequest request) {
        // 判断是不是直播间的主播
        checkIsOwnerRoom(request.getRoomId());
        RoomEntity room = roomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("未查询到该直播间 - " + request.getRoomId());
        }
        if (RoomStatusEnum.LIVING.getCode() == room.getStatus()) {
            throw new BusinessException("直播间已开播");
        }
        LambdaUpdateWrapper<RoomEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RoomEntity::getRoomId, request.getRoomId())
                .set(RoomEntity::getStatus, RoomStatusEnum.LIVING.getCode());
        roomMapper.update(null, wrapper);
        log.info("直播间 [{}] 已开播", request.getRoomId());
    }

    @Override
    public void closeRoom(RoomIdRequest request) {
        // 判断是不是直播间的主播
        checkIsOwnerRoom(request.getRoomId());
        RoomEntity room = roomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("未查询到该直播间 - " + request.getRoomId());
        }
        if (RoomStatusEnum.LIVING.getCode() == room.getStatus()) {
            throw new BusinessException("直播间已开播");
        }
        LambdaUpdateWrapper<RoomEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RoomEntity::getRoomId, request.getRoomId())
                .set(RoomEntity::getStatus, RoomStatusEnum.CLOSED.getCode());
        roomMapper.update(null, wrapper);
        log.info("直播间 [{}] 已关闭", request.getRoomId());
    }

    @Override
    public List<RoomDto> queryRoomList(RoomQueryRequest request) {
        LambdaQueryWrapper<RoomEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .eq(request.getRoomId() != null, RoomEntity::getRoomId, request.getRoomId())
                .like(StrUtil.isNotEmpty(request.getTitle()), RoomEntity::getTitle, request.getTitle())
                .orderByDesc(RoomEntity::getCreateTime);
        List<RoomEntity> rooms = roomMapper.selectList(wrapper);
        log.info("查询直播间列表: size={}", rooms.size());
        return rooms.stream().map(this::buildRoomDto).toList();
    }

    @Override
    public List<RoomDto> queryRoomListWeb(RoomQueryWebRequest request) {
        LambdaQueryWrapper<RoomEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getDeleted() != null, RoomEntity::getDeleted, request.getDeleted())
                .eq(request.getRoomId() != null, RoomEntity::getRoomId, request.getRoomId())
                .eq(request.getStatus() != null, RoomEntity::getStatus, request.getStatus())
                .like(StrUtil.isNotEmpty(request.getTitle()), RoomEntity::getTitle, request.getTitle())
                .orderByDesc(RoomEntity::getCreateTime);
        List<RoomEntity> rooms = roomMapper.selectList(wrapper);
        log.info("查询直播间列表: size={}", rooms.size());
        return rooms.stream().map(this::buildRoomDto).toList();
    }

    /**
     * 校验操作权限：主播只能操作自己的直播间（通过关联表判断），管理员/系统管理员可操作任意直播间
     */
    private void checkIsOwnerRoom(Long roomId) {
        if (UserInfoContext.getRole() == RoleTypeEnum.ANCHOR.getCode()) {
            LambdaQueryWrapper<UserRoom> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoom::getUserId, UserInfoContext.getUserId())
                    .eq(UserRoom::getRoomId, roomId)
                    .eq(UserRoom::getDeleted, DeleteEnum.ACTIVE.getCode());
            Long count = userRoomMapper.selectCount(wrapper);
            if (count == null || count == 0) {
                throw new BusinessException("无权操作该直播间，操作人不是本直播间主播");
            }
        }
    }

    private RoomDto buildRoomDto(RoomEntity room) {
        RoomDto dto = new RoomDto();
        BeanUtil.copyProperties(room, dto);
        // 推流/拉流地址是派生数据，按当前配置动态拼接，避免域名或鉴权 key 变更后需要刷库
        dto.setStreamUrl(liveStreamConfig.buildPushUrl(room.getStreamName()));
        dto.setPlayUrl(liveStreamConfig.buildPlayUrl(room.getStreamName()));
        return dto;
    }

}
