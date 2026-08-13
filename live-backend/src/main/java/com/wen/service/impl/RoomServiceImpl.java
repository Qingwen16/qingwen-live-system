package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.common.enums.RoomStatus;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.exception.BusinessException;
import com.wen.mapper.RoomMapper;
import com.wen.model.entity.RoomEntity;
import com.wen.model.dto.RoomDto;
import com.wen.model.vo.RoomQueryRequest;
import com.wen.model.vo.RoomRequest;
import com.wen.service.RoomService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        return getRoomInfo(request.getId());
    }

    @Override
    public void closeRoom(Long roomId) {
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
    public IPage<RoomDto> getRoomList(RoomQueryRequest request) {
        int pageNum = request.getPageNum() == null || request.getPageNum() < 1 ? 1 : request.getPageNum();
        int pageSize = request.getPageSize() == null || request.getPageSize() < 1 ? 10 : request.getPageSize();

        LambdaQueryWrapper<RoomEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(request.getTitle()), RoomEntity::getTitle, request.getTitle())
                .eq(request.getStatus() != null, RoomEntity::getStatus, request.getStatus())
                .eq(request.getCategoryId() != null, RoomEntity::getCategoryId, request.getCategoryId())
                .eq(request.getIsRecommend() != null, RoomEntity::getIsRecommend, request.getIsRecommend())
                .orderByDesc(RoomEntity::getCreateTime);

        IPage<RoomEntity> roomPage = roomMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        log.info("查询直播间列表: total={}, size={}", roomPage.getTotal(), roomPage.getRecords().size());
        return roomPage.convert(this::toDto);
    }

    @Override
    public RoomDto getRoomInfo(Long roomId) {
        if (roomId == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        return toDto(getExistRoom(roomId));
    }

    /**
     * 校验直播间是否存在
     */
    private RoomEntity getExistRoom(Long roomId) {
        RoomEntity room = roomMapper.selectOne(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getId, roomId));
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
