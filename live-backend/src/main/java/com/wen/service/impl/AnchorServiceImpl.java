package com.wen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.StatusEnum;
import com.wen.common.exception.BusinessException;
import com.wen.model.entity.AnchorEntity;
import com.wen.model.entity.UserEntity;
import com.wen.model.dto.AnchorDto;
import com.wen.model.dto.UserDto;
import com.wen.mapper.AnchorMapper;
import com.wen.service.AnchorService;
import com.wen.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnchorServiceImpl implements AnchorService {

    private final UserService userService;

    private final AnchorMapper anchorMapper;

    @Override
    public String registerAnchor(AnchorDto request) {
        if (request.getPhone() == null) {
            throw new BusinessException("注册手机号不能为空");
        }
        UserDto userInfo = userService.registerUser(request.getPhone());
        if (userInfo.getStatus() == StatusEnum.DISABLED.getCode()) {
            return "该用户状态已被设置为禁用";
        }
        if (userInfo.getDeleted() == DeleteEnum.DELETED.getCode()) {
            return "该用户账号已被设置为删除";
        }

        AnchorEntity anchorEntity = queryAnchorByUserId(userInfo.getUserId());

        if (anchorEntity != null) {
            return "使用此手机号的用户已经注册主播";
        }

        AnchorEntity anchor = new AnchorEntity();
        anchor.setUserId(userInfo.getUserId());
        anchor.setPhone(userInfo.getPhone());
        anchor.setNickname(request.getNickname());
        anchor.setCoverUrl(userInfo.getAvatar());
        anchor.setIntroduction(request.getIntroduction());
        anchor.setRoomId(request.getRoomId());
        anchor.setTotalLiveHours(0L);
        anchor.setTotalIncome(new BigDecimal("0.00"));
        anchor.setRemark(request.getRemark());
        anchor.setCreateTime(System.currentTimeMillis());
        anchor.setUpdateTime(System.currentTimeMillis());
        anchor.setCreateBy(request.getCreateBy());
        anchor.setUpdateBy(request.getCreateBy());
        anchor.setStatus(request.getStatus());
        anchor.setDeleted(request.getDeleted());

        anchorMapper.insert(anchor);
        return "已成功注册主播";
    }

    @Override
    public AnchorEntity queryAnchorByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        LambdaQueryWrapper<AnchorEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnchorEntity::getUserId, userId);
        AnchorEntity anchorEntity = anchorMapper.selectOne(wrapper);
        log.info("查询到主播信息: [{}]", anchorEntity);
        return anchorEntity;
    }

    @Override
    public List<AnchorDto> queryAnchor() {
        LambdaQueryWrapper<AnchorEntity> wrapper = new LambdaQueryWrapper<>();
        List<AnchorEntity> anchorList = anchorMapper.selectList(wrapper);

        Set<Long> userIdSet = anchorList.stream().map(AnchorEntity::getUserId).collect(Collectors.toSet());
        List<UserEntity> infoList = userService.queryByUserIdSet(userIdSet);

        List<AnchorDto> dtoList = new ArrayList<>();
        for (UserEntity user : infoList) {
            for (AnchorEntity anchor : anchorList) {
                if (user.getUserId().equals(anchor.getUserId())) {
                    AnchorDto dto = new AnchorDto();
                    dto.setUserId(user.getUserId());
                    dto.setPhone(user.getPhone());
                    dto.setNickname(anchor.getNickname());
                    dto.setRealName(user.getUsername());
                    dto.setGender(user.getGender());
                    dto.setAvatarUrl(user.getAvatar());
                    dto.setCoverUrl(anchor.getCoverUrl());
                    dto.setIntroduction(anchor.getIntroduction());
                    dto.setRoomId(anchor.getRoomId());
                    dto.setTotalLiveHours(anchor.getTotalLiveHours());
                    dto.setTotalIncome(anchor.getTotalIncome());
                    dto.setProvince(user.getProvince());
                    dto.setCity(user.getCity());
                    dto.setAddress(user.getAddress());
                    dto.setRemark(anchor.getRemark());
                    dto.setCreateTime(anchor.getCreateTime());
                    dto.setUpdateTime(anchor.getUpdateTime());
                    dto.setCreateBy(anchor.getCreateBy());
                    dto.setUpdateBy(anchor.getUpdateBy());
                    dto.setStatus(anchor.getStatus());
                    dto.setDeleted(anchor.getDeleted());
                    dtoList.add(dto);
                }
            }
        }

        log.info("查询到主播信息数量: [{}]", dtoList.size());
        return dtoList;
    }

}