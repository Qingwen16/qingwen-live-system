package com.wen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.GoodStatusEnum;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.exception.BusinessException;
import com.wen.mapper.UserRoomMapper;
import com.wen.mapper.GoodMapper;
import com.wen.mapper.RoomMapper;
import com.wen.model.dto.GoodDto;
import com.wen.model.entity.UserRoom;
import com.wen.model.entity.GoodEntity;
import com.wen.model.entity.RoomEntity;
import com.wen.model.vo.*;
import com.wen.service.GoodService;
import com.wen.service.RoleService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoodServiceImpl implements GoodService {

    private static final int MAX_ROOM_GOODS = 3;

    private final GoodMapper goodMapper;

    private final RoomMapper roomMapper;

    private final UserRoomMapper relationMapper;

    private final RoleService roleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGood(GoodCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BusinessException("商品名称不能为空");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        checkRoomExist(request.getRoomId());

        long currentTime = System.currentTimeMillis();
        GoodEntity good = new GoodEntity();
        BeanUtils.copyProperties(request, good);
        good.setStatus(GoodStatusEnum.NOT_LISTED.getCode());
        good.setSalesCount(0);
        good.setDeleted(DeleteEnum.ACTIVE.getCode());
        good.setCreateTime(currentTime);
        good.setUpdateTime(currentTime);
        goodMapper.insert(good);
        log.info("新增商品成功: goodId={}", good.getId());
        return good.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGood(GoodUpdateRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        getExistGood(request.getId());
        checkRoomExist(request.getRoomId());

        GoodEntity good = new GoodEntity();
        BeanUtils.copyProperties(request, good);
        good.setUpdateTime(System.currentTimeMillis());
        goodMapper.updateById(good);
        log.info("更新商品成功: goodId={}", good.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGood(GoodIdRequest request) {
        Long goodId = request.getGoodId();
        getExistGood(goodId);
        goodMapper.update(null, new LambdaUpdateWrapper<GoodEntity>()
                .eq(GoodEntity::getId, goodId)
                .set(GoodEntity::getDeleted, DeleteEnum.DELETED.getCode())
                .set(GoodEntity::getUpdateTime, System.currentTimeMillis()));
        log.info("删除商品成功: goodId={}", goodId);
    }

    @Override
    public List<GoodDto> queryGoods(GoodQueryRequest request) {
        LambdaQueryWrapper<GoodEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .like(request.getName() != null && !request.getName().isBlank(),
                        GoodEntity::getName, request.getName())
                .eq(request.getStatus() != null, GoodEntity::getStatus, request.getStatus())
                .eq(request.getRoomId() != null, GoodEntity::getRoomId, request.getRoomId())
                .orderByDesc(GoodEntity::getCreateTime);

        return goodMapper.selectList(wrapper).stream().map(this::toDto).toList();
    }


    @Override
    public List<GoodDto> queryAppGoods() {
        LambdaQueryWrapper<GoodEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .eq(GoodEntity::getStatus, GoodStatusEnum.LISTED.getCode())
                .orderByDesc(GoodEntity::getCreateTime);

        return goodMapper.selectList(wrapper).stream().map(this::toDto).toList();
    }

    @Override
    public List<GoodDto> queryRoomGoods(RoomIdRequest request) {
        if (request.getRoomId() == null) {
            throw new BusinessException("直播间ID不能为空");
        }
        LambdaQueryWrapper<GoodEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodEntity::getRoomId, request.getRoomId())
                .eq(GoodEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .eq(GoodEntity::getStatus, GoodStatusEnum.LISTED.getCode())
                .gt(GoodEntity::getStockCount, 0)
                .orderByDesc(GoodEntity::getCreateTime);
        return goodMapper.selectList(wrapper).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onShelf(GoodIdRequest request) {
        Long goodId = request.getGoodId();
        GoodEntity good = getExistGood(goodId);
        if (GoodStatusEnum.LISTED.getCode() == good.getStatus()) {
            throw new BusinessException("商品已上架");
        }
        goodMapper.update(null, new LambdaUpdateWrapper<GoodEntity>()
                .eq(GoodEntity::getId, goodId)
                .set(GoodEntity::getStatus, GoodStatusEnum.LISTED.getCode())
                .set(GoodEntity::getUpdateTime, System.currentTimeMillis()));
        log.info("商品上架成功: goodId={}", goodId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offShelf(GoodIdRequest request) {
        Long goodId = request.getGoodId();
        GoodEntity good = getExistGood(goodId);
        if (GoodStatusEnum.NOT_LISTED.getCode() == good.getStatus()) {
            throw new BusinessException("商品已下架");
        }
        goodMapper.update(null, new LambdaUpdateWrapper<GoodEntity>()
                .eq(GoodEntity::getId, goodId)
                .set(GoodEntity::getStatus, GoodStatusEnum.NOT_LISTED.getCode())
                .set(GoodEntity::getUpdateTime, System.currentTimeMillis()));
        log.info("商品下架成功: goodId={}", goodId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mountToRoom(GoodIdRequest request) {
        Long goodId = request.getGoodId();
        RoomEntity room = getAnchorRoom();
        GoodEntity good = getExistGood(goodId);
        if (GoodStatusEnum.LISTED.getCode() != good.getStatus()) {
            throw new BusinessException("商品未上架，无法挂载");
        }
        if (good.getStockCount() == null || good.getStockCount() <= 0) {
            throw new BusinessException("商品缺货，无法挂载");
        }
        Long mountedCount = goodMapper.selectCount(new LambdaQueryWrapper<GoodEntity>()
                .eq(GoodEntity::getRoomId, room.getRoomId())
                .eq(GoodEntity::getDeleted, DeleteEnum.ACTIVE.getCode()));
        if (mountedCount != null && mountedCount >= MAX_ROOM_GOODS) {
            throw new BusinessException("一个直播间最多挂载" + MAX_ROOM_GOODS + "个商品");
        }
        goodMapper.update(null, new LambdaUpdateWrapper<GoodEntity>()
                .eq(GoodEntity::getId, goodId)
                .set(GoodEntity::getRoomId, room.getRoomId())
                .set(GoodEntity::getUpdateTime, System.currentTimeMillis()));
        log.info("商品挂载到直播间成功: goodId={}, roomId={}", goodId, room.getRoomId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unmountFromRoom(GoodIdRequest request) {
        Long goodId = request.getGoodId();
        RoomEntity room = getAnchorRoom();
        GoodEntity good = getExistGood(goodId);
        if (!room.getRoomId().equals(good.getRoomId())) {
            throw new BusinessException("该商品未挂载到您的直播间");
        }
        goodMapper.update(null, new LambdaUpdateWrapper<GoodEntity>()
                .eq(GoodEntity::getId, goodId)
                .set(GoodEntity::getRoomId, null)
                .set(GoodEntity::getUpdateTime, System.currentTimeMillis()));
        log.info("商品从直播间移除成功: goodId={}, roomId={}", goodId, room.getRoomId());
    }

    /**
     * 查询当前主播的直播间（关联表按 id 倒序取最近一条）
     */
    private RoomEntity getAnchorRoom() {
        Long userId = currentUserId();
        Integer role = roleService.queryRoleByUserId(userId);
        if (role == null || role != RoleTypeEnum.ANCHOR.getCode()) {
            throw new BusinessException("您还不是主播");
        }
        List<UserRoom> relations = relationMapper.selectList(new LambdaQueryWrapper<UserRoom>()
                .eq(UserRoom::getUserId, userId)
                .eq(UserRoom::getDeleted, DeleteEnum.ACTIVE.getCode())
                .orderByDesc(UserRoom::getId));
        if (relations.isEmpty()) {
            throw new BusinessException("请先创建直播间");
        }
        RoomEntity room = roomMapper.selectOne(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getRoomId, relations.get(0).getRoomId())
                .eq(RoomEntity::getDeleted, DeleteEnum.ACTIVE.getCode()));
        if (room == null) {
            throw new BusinessException("直播间不存在");
        }
        return room;
    }

    private Long currentUserId() {
        Long userId = UserInfoContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        return userId;
    }

    /**
     * 查询未删除的商品，不存在则抛异常
     */
    private GoodEntity getExistGood(Long goodId) {
        if (goodId == null) {
            throw new BusinessException("商品ID不能为空");
        }
        GoodEntity good = goodMapper.selectOne(new LambdaQueryWrapper<GoodEntity>()
                .eq(GoodEntity::getId, goodId)
                .eq(GoodEntity::getDeleted, DeleteEnum.ACTIVE.getCode()));
        if (good == null) {
            throw new BusinessException("商品不存在");
        }
        return good;
    }

    /**
     * 校验关联直播间是否存在（未删除）
     */
    private void checkRoomExist(Long roomId) {
        if (roomId == null) {
            return;
        }
        RoomEntity room = roomMapper.selectOne(new LambdaQueryWrapper<RoomEntity>()
                .eq(RoomEntity::getRoomId, roomId)
                .eq(RoomEntity::getDeleted, DeleteEnum.ACTIVE.getCode()));
        if (room == null) {
            throw new BusinessException("直播间不存在");
        }
    }

    private GoodDto toDto(GoodEntity good) {
        GoodDto dto = new GoodDto();
        BeanUtils.copyProperties(good, dto);
        return dto;
    }
}
