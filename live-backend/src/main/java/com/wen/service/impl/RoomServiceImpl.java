package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.RoomStatus;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.exception.BusinessException;
import com.wen.mapper.RoomMapper;
import com.wen.model.entity.RoomEntity;
import com.wen.model.dto.RoomDto;
import com.wen.model.vo.RoomIdRequest;
import com.wen.model.vo.RoomOnlineCountVo;
import com.wen.model.vo.RoomQueryRequest;
import com.wen.model.vo.RoomRequest;
import com.wen.service.RoomService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public RoomDto createRoom(RoomRequest request) {
        Long anchorId = currentUserId();
        if (StrUtil.isEmpty(request.getTitle())) {
            throw new BusinessException("直播间标题不能为空");
        }
        // 一个主播只允许创建一个直播间
        Long existCount = roomMapper.selectCount(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getAnchorId, anchorId));
        if (existCount != null && existCount > 0) {
            throw new BusinessException("每个主播只能创建一个直播间");
        }

        long currentTime = System.currentTimeMillis();
        RoomEntity room = new RoomEntity();
        room.setRoomNumber(generateRoomNumber());
        room.setAnchorId(anchorId);
        room.setTitle(request.getTitle());
        room.setCoverImage(request.getCoverImage());
        room.setCategoryId(request.getCategoryId());
        room.setAnnouncement(request.getAnnouncement());
        room.setTags(request.getTags());
        room.setCurrentViewers(0);
        room.setTotalViewers(0L);
        room.setLikeCount(0L);
        room.setFollowCount(0L);
        room.setStatus(RoomStatus.NOT_STARTED.getCode());
        room.setIsRecommend(0);
        room.setCreateTime(currentTime);
        room.setUpdateTime(currentTime);
        roomMapper.insert(room);

        log.info("主播 [{}] 创建直播间成功: roomNumber={}", anchorId, room.getRoomNumber());
        return toDto(room);
    }

    @Override
    public RoomDto updateRoom(RoomRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        RoomEntity room = getExistRoom(request.getId());
        checkOwnership(room);

        LambdaUpdateWrapper<RoomEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RoomEntity::getId, request.getId());
        if (StrUtil.isNotEmpty(request.getTitle())) {
            wrapper.set(RoomEntity::getTitle, request.getTitle());
        }
        if (request.getCoverImage() != null) {
            wrapper.set(RoomEntity::getCoverImage, request.getCoverImage());
        }
        if (request.getCategoryId() != null) {
            wrapper.set(RoomEntity::getCategoryId, request.getCategoryId());
        }
        if (request.getAnnouncement() != null) {
            wrapper.set(RoomEntity::getAnnouncement, request.getAnnouncement());
        }
        if (request.getTags() != null) {
            wrapper.set(RoomEntity::getTags, request.getTags());
        }
        wrapper.set(RoomEntity::getUpdateTime, System.currentTimeMillis());
        roomMapper.update(null, wrapper);

        log.info("直播间 [{}] 更新成功", request.getId());
        RoomIdRequest roomIdRequest = new RoomIdRequest();
        roomIdRequest.setRoomId(request.getId());
        return getRoomInfo(roomIdRequest);
    }

    @Override
    public RoomDto deleteRoom(RoomRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        RoomEntity room = getExistRoom(request.getId());
        checkOwnership(room);

        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getId, request.getId())
                .set(RoomEntity::getDeleted, DeleteEnum.DELETED.getCode())
                .set(RoomEntity::getUpdateTime, System.currentTimeMillis()));
        log.info("直播间 [{}] 已删除", request.getId());
        return toDto(room);
    }

    @Override
    public void openRoom(RoomIdRequest request) {
        Long roomId = request.getRoomId();
        if (roomId == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        RoomEntity room = getExistRoom(roomId);
        checkOwnership(room);
        if (RoomStatus.LIVING.getCode() == room.getStatus()) {
            throw new BusinessException("直播间已开播");
        }

        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getId, roomId)
                .set(RoomEntity::getStatus, RoomStatus.LIVING.getCode())
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
                .eq(RoomEntity::getId, roomId)
                .set(RoomEntity::getStatus, RoomStatus.CLOSED.getCode())
                .set(RoomEntity::getEndTime, System.currentTimeMillis())
                .set(RoomEntity::getUpdateTime, System.currentTimeMillis()));

        log.info("直播间 [{}] 已关闭", roomId);
    }

    @Override
    public List<RoomDto> getRoomList(RoomQueryRequest request) {
        LambdaQueryWrapper<RoomEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .like(StrUtil.isNotEmpty(request.getTitle()), RoomEntity::getTitle, request.getTitle())
                .eq(request.getStatus() != null, RoomEntity::getStatus, request.getStatus())
                .eq(request.getCategoryId() != null, RoomEntity::getCategoryId, request.getCategoryId())
                .eq(request.getIsRecommend() != null, RoomEntity::getIsRecommend, request.getIsRecommend())
                .orderByDesc(RoomEntity::getCreateTime);

        List<RoomEntity> rooms = roomMapper.selectList(wrapper);
        log.info("查询直播间列表: size={}", rooms.size());
        return rooms.stream().map(this::toDto).toList();
    }

    @Override
    public RoomDto getRoomInfo(RoomIdRequest request) {
        if (request.getRoomId() == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        return toDto(getExistRoom(request.getRoomId()));
    }

    @Override
    public void incrementViewers(Long roomId) {
        if (roomId == null) {
            return;
        }
        // 用 SQL 自增，避免并发下读-改-写导致计数不准
        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getId, roomId)
                .setSql("current_viewers = current_viewers + 1, total_viewers = total_viewers + 1"));
    }

    @Override
    public void decrementViewers(Long roomId) {
        if (roomId == null) {
            return;
        }
        roomMapper.update(null, new LambdaUpdateWrapper<RoomEntity>()
                .eq(RoomEntity::getId, roomId)
                .setSql("current_viewers = GREATEST(current_viewers - 1, 0)"));
    }

    @Override
    public List<RoomOnlineCountVo> getOnlineCounts() {
        // 只查询 id 和 current_viewers 两列，降低轮询 payload 与 DB 开销
        List<RoomEntity> rooms = roomMapper.selectList(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .select(RoomEntity::getId, RoomEntity::getCurrentViewers));
        return rooms.stream().map(room -> {
            RoomOnlineCountVo vo = new RoomOnlineCountVo();
            vo.setRoomId(room.getId());
            vo.setCurrentViewers(room.getCurrentViewers());
            return vo;
        }).toList();
    }

    /**
     * 校验直播间是否存在
     */
    private RoomEntity getExistRoom(Long roomId) {
        RoomEntity room = roomMapper.selectOne(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getId, roomId)
                .eq(RoomEntity::getDeleted, DeleteEnum.ACTIVE.getCode()));
        if (room == null) {
            throw new BusinessException("直播间不存在");
        }
        return room;
    }

    /**
     * 校验操作权限：主播只能操作自己的直播间，管理员/系统管理员可操作任意直播间
     */
    private void checkOwnership(RoomEntity room) {
        Integer role = UserInfoContext.getRole();
        if (role != null && (role == RoleTypeEnum.ADMIN.getCode() || role == RoleTypeEnum.SUPER_ADMIN.getCode())) {
            return;
        }
        Long userId = currentUserId();
        if (!room.getAnchorId().equals(userId)) {
            throw new BusinessException("无权操作该直播间");
        }
    }

    private Long currentUserId() {
        Long userId = UserInfoContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已失效");
        }
        return userId;
    }

    private RoomDto toDto(RoomEntity room) {
        RoomDto dto = new RoomDto();
        BeanUtil.copyProperties(room, dto);
        return dto;
    }

    /**
     * 生成房间号：时间戳(毫秒) + 3 位随机数
     */
    private String generateRoomNumber() {
        return String.valueOf(System.currentTimeMillis() * 1000 + new Random().nextInt(1000));
    }
}
