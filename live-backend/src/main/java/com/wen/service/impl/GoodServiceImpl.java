package com.wen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.enums.GoodStatusEnum;
import com.wen.common.exception.BusinessException;
import com.wen.model.vo.GoodCreateRequest;
import com.wen.model.entity.GoodEntity;
import com.wen.model.dto.GoodDto;
import com.wen.mapper.GoodMapper;
import com.wen.model.vo.GoodUpdateRequest;
import com.wen.service.GoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createGood(GoodCreateRequest request) {
        log.info("新增货物信息 - 货物名称: [{}]", request.getName());
        // 转换DTO为实体
        GoodEntity goodEntity = new GoodEntity();
        BeanUtils.copyProperties(request, goodEntity);
        // 设置创建时间和更新时间
        long currentTime = System.currentTimeMillis();
        goodEntity.setCreateTime(currentTime);
        goodEntity.setUpdateTime(currentTime);
        goodEntity.setStatus(GoodStatusEnum.NOT_LISTED.getCode());
        goodEntity.setSalesCount(0);
        // 插入数据库
        goodMapper.insert(goodEntity);
        log.info("新增货物成功 - 货物ID: [{}]", goodEntity.getId());
        return "新增货物成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateGood(GoodUpdateRequest request) {
        log.info("更新货物信息 - 货物ID: [{}]", request.getName());
        GoodEntity goodEntity = goodMapper.selectById(request.getId());
        BeanUtils.copyProperties(request, goodEntity);
        goodEntity.setUpdateTime(System.currentTimeMillis());
        // 更新数据库
        goodMapper.updateById(goodEntity);
        log.info("更新货物成功，货物ID：{}", goodEntity.getId());
        return "更新货物成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteGood(Long goodId) {
        goodMapper.deleteById(goodId);
        log.info("删除货物成功，货物ID：{}", goodId);
        return "删除货物成功";
    }

    @Override
    public GoodEntity queryGoodById(Long goodId) {
        if (goodId == null) {
            throw new BusinessException("查询参数的商品ID不能为空");
        }
        return goodMapper.selectById(goodId);
    }

    @Override
    public List<GoodDto> queryTotalGoods() {
        LambdaQueryWrapper<GoodEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(GoodEntity::getCreateTime);
        List<GoodEntity> infoList = goodMapper.selectList(wrapper);

        List<GoodDto> dtoList = new ArrayList<>();
        for (GoodEntity goodEntity : infoList) {
            GoodDto dto = new GoodDto();
            BeanUtils.copyProperties(goodEntity, dto);
            dtoList.add(dto);
        }

        log.info("查询到的商品数量为 [{}]", dtoList.size());
        return dtoList;
    }

    @Override
    public List<GoodDto> queryTotalListedGoods() {
        LambdaQueryWrapper<GoodEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodEntity::getStatus, GoodStatusEnum.LISTED.getCode());
        wrapper.orderByDesc(GoodEntity::getCreateTime);
        List<GoodEntity> infoList = goodMapper.selectList(wrapper);

        List<GoodDto> dtoList = new ArrayList<>();
        for (GoodEntity goodEntity : infoList) {
            GoodDto dto = new GoodDto();
            BeanUtils.copyProperties(goodEntity, dto);
            dtoList.add(dto);
        }

        log.info("查询到的商品数量为 [{}]", dtoList.size());
        return dtoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reduceGoodStock(Long goodId, Integer quantity) {
        LambdaUpdateWrapper<GoodEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSql("stock_count = stock_count - " + quantity);
        wrapper.setSql("sales_count = sales_count + " + quantity);
        wrapper.eq(GoodEntity::getId, goodId);
        wrapper.ge(GoodEntity::getStockCount, quantity);
        return goodMapper.update(null, wrapper) > 0;
    }
}