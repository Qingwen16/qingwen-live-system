package com.wen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.common.exception.BusinessException;
import com.wen.common.response.PageResult;
import com.wen.mapper.AddressMapper;
import com.wen.model.entity.AddressEntity;
import com.wen.model.vo.AddressQueryRequest;
import com.wen.service.AddressWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 后台地址管理服务实现（Web 管理端）
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@Service
@RequiredArgsConstructor
public class AddressWebServiceImpl implements AddressWebService {

    private static final long MAX_PAGE_SIZE = 100;

    private final AddressMapper addressMapper;

    @Override
    public PageResult<AddressEntity> pageQuery(AddressQueryRequest request) {
        long pageNum = request.getPageNum() < 1 ? 1 : request.getPageNum();
        long pageSize = request.getPageSize() < 1 ? 10 : request.getPageSize();
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);

        LambdaQueryWrapper<AddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getUserId() != null, AddressEntity::getUserId, request.getUserId())
                .like(StrUtil.isNotBlank(request.getName()), AddressEntity::getName, request.getName())
                .like(StrUtil.isNotBlank(request.getPhone()), AddressEntity::getPhone, request.getPhone())
                .eq(request.getIsDefault() != null, AddressEntity::getIsDefault, request.getIsDefault())
                .orderByDesc(AddressEntity::getCreateTime);

        Page<AddressEntity> page = addressMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long id) {
        if (id == null) {
            throw new BusinessException("地址ID不能为空");
        }
        if (addressMapper.selectById(id) == null) {
            throw new BusinessException("地址不存在");
        }
        addressMapper.deleteById(id);
    }
}
