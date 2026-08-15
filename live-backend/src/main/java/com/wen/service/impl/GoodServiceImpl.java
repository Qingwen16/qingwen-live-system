package com.wen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.GoodStatusEnum;
import com.wen.common.exception.BusinessException;
import com.wen.mapper.GoodMapper;
import com.wen.mapper.RoomMapper;
import com.wen.model.dto.GoodDto;
import com.wen.model.entity.GoodEntity;
import com.wen.model.entity.RoomEntity;
import com.wen.model.vo.*;
import com.wen.service.GoodService;
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

    private final GoodMapper goodMapper;

    private final RoomMapper roomMapper;

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
                .eq(RoomEntity::getId, roomId)
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
