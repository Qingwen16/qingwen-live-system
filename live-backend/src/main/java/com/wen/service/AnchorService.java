package com.wen.service;

import com.wen.model.entity.AnchorEntity;
import com.wen.model.dto.AnchorDto;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface AnchorService {

    /**
     * 注册主播
     */
    String registerAnchor(AnchorDto anchorDto);

    /**
     * 根据用户ID查询主播信息
     */
    AnchorEntity queryAnchorByUserId(Long userId);

    /**
     * 批量查询主播信息
     */
    List<AnchorDto> queryAnchor();

}
