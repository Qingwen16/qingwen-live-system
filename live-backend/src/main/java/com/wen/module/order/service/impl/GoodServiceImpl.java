package com.wen.module.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.enums.GoodStatusEnum;
import com.wen.common.exception.BusinessException;
import com.wen.module.order.domain.vo.GoodCreateRequest;
import com.wen.module.order.domain.entity.GoodInfo;
import com.wen.module.order.domain.vo.GoodInfoDto;
import com.wen.module.order.mapper.GoodInfoMapper;
import com.wen.module.order.domain.vo.GoodUpdateRequest;
import com.wen.module.order.service.GoodService;
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

    private final GoodInfoMapper goodInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createGood(GoodCreateRequest request) {
        log.info("新增货物信息 - 货物名称: [{}]", request.getName());
        // 转换DTO为实体
        GoodInfo goodInfo = new GoodInfo();
        BeanUtils.copyProperties(request, goodInfo);
        // 设置创建时间和更新时间
        long currentTime = System.currentTimeMillis();
        goodInfo.setCreateTime(currentTime);
        goodInfo.setUpdateTime(currentTime);
        goodInfo.setStatus(GoodStatusEnum.NOT_LISTED.getCode());
        goodInfo.setSalesCount(0);
        // 插入数据库
        goodInfoMapper.insert(goodInfo);
        log.info("新增货物成功 - 货物ID: [{}]", goodInfo.getId());
        return "新增货物成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateGood(GoodUpdateRequest request) {
        log.info("更新货物信息 - 货物ID: [{}]", request.getName());
        GoodInfo goodInfo = goodInfoMapper.selectById(request.getId());
        BeanUtils.copyProperties(request, goodInfo);
        goodInfo.setUpdateTime(System.currentTimeMillis());
        // 更新数据库
        goodInfoMapper.updateById(goodInfo);
        log.info("更新货物成功，货物ID：{}", goodInfo.getId());
        return "更新货物成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteGood(Long goodId) {
        goodInfoMapper.deleteById(goodId);
        log.info("删除货物成功，货物ID：{}", goodId);
        return "删除货物成功";
    }

    @Override
    public GoodInfo queryGoodById(Long goodId) {
        if (goodId == null) {
            throw new BusinessException("查询参数的商品ID不能为空");
        }
        return goodInfoMapper.selectById(goodId);
    }

    @Override
    public List<GoodInfoDto> queryTotalGoods() {
        LambdaQueryWrapper<GoodInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(GoodInfo::getCreateTime);
        List<GoodInfo> infoList = goodInfoMapper.selectList(wrapper);

        List<GoodInfoDto> dtoList = new ArrayList<>();
        for (GoodInfo goodInfo : infoList) {
            GoodInfoDto dto = new GoodInfoDto();
            BeanUtils.copyProperties(goodInfo, dto);
            dtoList.add(dto);
        }

        log.info("查询到的商品数量为 [{}]", dtoList.size());
        return dtoList;
    }

    @Override
    public List<GoodInfoDto> queryTotalListedGoods() {
        LambdaQueryWrapper<GoodInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodInfo::getStatus, GoodStatusEnum.LISTED.getCode());
        wrapper.orderByDesc(GoodInfo::getCreateTime);
        List<GoodInfo> infoList = goodInfoMapper.selectList(wrapper);

        List<GoodInfoDto> dtoList = new ArrayList<>();
        for (GoodInfo goodInfo : infoList) {
            GoodInfoDto dto = new GoodInfoDto();
            BeanUtils.copyProperties(goodInfo, dto);
            dtoList.add(dto);
        }

        log.info("查询到的商品数量为 [{}]", dtoList.size());
        return dtoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reduceGoodStock(Long goodId, Integer quantity) {
        LambdaUpdateWrapper<GoodInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSql("stock_count = stock_count - " + quantity);
        wrapper.setSql("sales_count = sales_count + " + quantity);
        wrapper.eq(GoodInfo::getId, goodId);
        wrapper.ge(GoodInfo::getStockCount, quantity);
        return goodInfoMapper.update(null, wrapper) > 0;
    }
}