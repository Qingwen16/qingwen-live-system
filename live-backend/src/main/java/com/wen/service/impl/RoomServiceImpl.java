package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import com.wen.model.vo.RoomIdRequest;
import com.wen.model.vo.RoomOnlineCountVo;
import com.wen.model.vo.RoomGetRequest;
import com.wen.model.vo.RoomRequest;
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

    private final UserRoomMapper relationMapper;

    private final RoleService roleService;

    private final RoomIdGenerator roomIdGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomDto createRoom(RoomRequest request) {
        Long userId = currentUserId();
        checkAnchor(userId);

        Long roomId = roomIdGenerator.generate();

        RoomEntity room = new RoomEntity();
        room.setRoomId(roomId);
        room.setStreamName(generateStreamName());
        room.setTitle(request.getTitle());
        room.setCurrentViewers(0);
        room.setTotalLiveHours(0L);
        room.setTotalIncome(new BigDecimal("0.00"));
        room.setStatus(RoomStatusEnum.NOT_STARTED.getCode());
        roomMapper.insert(room);

        UserRoom relation = new UserRoom();
        relation.setUserId(userId);
        relation.setRoomId(roomId);
        relationMapper.insert(relation);

        log.info("主播 [{}] 创建直播间成功: streamName={}", userId, room.getStreamName());
        return buildRoomDto(room);
    }

    @Override
    public void updateRoom(RoomRequest request) {
        RoomEntity room = getExistRoom(request.getRoomId());
        checkOwnership(room);

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
    public void deleteRoom(RoomRequest request) {
        if (request.getRoomId() == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        RoomEntity room = getExistRoom(request.getRoomId());
        checkOwnership(room);

        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getRoomId, request.getRoomId())
                .set(RoomEntity::getDeleted, DeleteEnum.DELETED.getCode())
                .set(RoomEntity::getUpdateTime, System.currentTimeMillis()));
        // 直播间删除时同步逻辑删除关联关系，避免残留无效关联
        relationMapper.update(null, new LambdaUpdateWrapper<UserRoom>()
                .eq(UserRoom::getRoomId, request.getRoomId())
                .eq(UserRoom::getDeleted, DeleteEnum.ACTIVE.getCode())
                .set(UserRoom::getDeleted, DeleteEnum.DELETED.getCode()));
        log.info("直播间 [{}] 已删除", request.getRoomId());
    }

    @Override
    public void openRoom(RoomIdRequest request) {
        Long roomId = request.getRoomId();
        if (roomId == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        RoomEntity room = getExistRoom(roomId);
        checkOwnership(room);
        if (RoomStatusEnum.LIVING.getCode() == room.getStatus()) {
            throw new BusinessException("直播间已开播");
        }

        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getRoomId, roomId)
                .set(RoomEntity::getStatus, RoomStatusEnum.LIVING.getCode())
                .set(RoomEntity::getCurrentViewers, 0)
                .set(RoomEntity::getStartTime, System.currentTimeMillis())
                .set(RoomEntity::getUpdateTime, System.currentTimeMillis()));

        log.info("直播间 [{}] 已开播", roomId);
    }

    @Override
    public void closeRoom(RoomIdRequest request) {
        Long roomId = request.getRoomId();
        if (roomId == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        RoomEntity room = getExistRoom(roomId);
        checkOwnership(room);

        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getRoomId, roomId)
                .set(RoomEntity::getStatus, RoomStatusEnum.CLOSED.getCode())
                .set(RoomEntity::getEndTime, System.currentTimeMillis())
                .set(RoomEntity::getUpdateTime, System.currentTimeMillis()));

        log.info("直播间 [{}] 已关闭", roomId);
    }

    @Override
    public List<RoomDto> getRoomList(RoomGetRequest request) {
        LambdaQueryWrapper<RoomEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .like(StrUtil.isNotEmpty(request.getTitle()), RoomEntity::getTitle, request.getTitle())
                .eq(request.getStatus() != null, RoomEntity::getStatus, request.getStatus())
                .orderByDesc(RoomEntity::getCreateTime);

        List<RoomEntity> rooms = roomMapper.selectList(wrapper);
        log.info("查询直播间列表: size={}", rooms.size());
        return rooms.stream().map(this::buildRoomDto).toList();
    }

    @Override
    public RoomDto getRoomInfo(RoomIdRequest request) {
        if (request.getRoomId() == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        return buildRoomDto(getExistRoom(request.getRoomId()));
    }

    @Override
    public void incrementViewers(Long roomId) {
        if (roomId == null) {
            return;
        }
        // 用 SQL 自增，避免并发下读-改-写导致计数不准
        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getRoomId, roomId)
                .setSql("current_viewers = current_viewers + 1"));
    }

    @Override
    public void decrementViewers(Long roomId) {
        if (roomId == null) {
            return;
        }
        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getRoomId, roomId)
                .setSql("current_viewers = GREATEST(current_viewers - 1, 0)"));
    }

    @Override
    public List<RoomOnlineCountVo> getOnlineCounts() {
        // 只查询 id 和 current_viewers 两列，降低轮询 payload 与 DB 开销
        List<RoomEntity> rooms = roomMapper.selectList(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .select(RoomEntity::getRoomId, RoomEntity::getCurrentViewers));
        return rooms.stream().map(room -> {
            RoomOnlineCountVo vo = new RoomOnlineCountVo();
            vo.setRoomId(room.getRoomId());
            vo.setCurrentViewers(room.getCurrentViewers());
            return vo;
        }).toList();
    }

    /**
     * 校验直播间是否存在
     */
    private RoomEntity getExistRoom(Long roomId) {
        RoomEntity room = roomMapper.selectOne(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getRoomId, roomId)
                .eq(RoomEntity::getDeleted, DeleteEnum.ACTIVE.getCode()));
        if (room == null) {
            throw new BusinessException("直播间不存在");
        }
        return room;
    }

    /**
     * 校验操作权限：主播只能操作自己的直播间（通过关联表判断），管理员/系统管理员可操作任意直播间
     */
    private void checkOwnership(RoomEntity room) {
        Integer role = UserInfoContext.getRole();
        if (role != null && (role == RoleTypeEnum.ADMIN.getCode() || role == RoleTypeEnum.SUPER_ADMIN.getCode())) {
            return;
        }
        Long userId = currentUserId();
        checkAnchor(userId);
        Long count = relationMapper.selectCount(new LambdaQueryWrapper<UserRoom>()
                .eq(UserRoom::getUserId, userId)
                .eq(UserRoom::getRoomId, room.getRoomId())
                .eq(UserRoom::getDeleted, DeleteEnum.ACTIVE.getCode()));
        if (count == null || count == 0) {
            throw new BusinessException("无权操作该直播间");
        }
    }

    /**
     * 校验当前用户是否为主播（角色为 ANCHOR）
     */
    private void checkAnchor(Long userId) {
        Integer role = roleService.queryRoleByUserId(userId);
        if (role == null || role != RoleTypeEnum.ANCHOR.getCode()) {
            throw new BusinessException("您还不是主播");
        }
    }

    private Long currentUserId() {
        Long userId = UserInfoContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已失效");
        }
        return userId;
    }

    private RoomDto buildRoomDto(RoomEntity room) {
        RoomDto dto = new RoomDto();
        BeanUtil.copyProperties(room, dto);
        return dto;
    }

    /**
     * 生成推流名：时间戳(毫秒) + 3 位随机数，落库前校验唯一，冲突则重新生成
     */
    private String generateStreamName() {
        for (int i = 0; i < 3; i++) {
            long name = System.currentTimeMillis() * 1000 + new Random().nextInt(1000);
            String streamName = String.valueOf(name);
            LambdaQueryWrapper<RoomEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RoomEntity::getStreamName, streamName);
            Long count = roomMapper.selectCount(wrapper);
            if (count == null || count == 0) {
                return streamName;
            }
            log.info("推流名 [{}] 已存在，重新生成", streamName);
        }
        throw new BusinessException("推流名生成失败，请重试");
    }
}
