package com.wen.module.user.service;

import com.wen.module.user.domain.entity.AnchorInfo;
import com.wen.module.user.domain.vo.AnchorInfoDto;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface AnchorService {

    /**
     * 注册主播
     */
    String registerAnchor(AnchorInfoDto anchorInfoDto);

    /**
     * 根据用户ID查询主播信息
     */
    AnchorInfo queryAnchorByUserId(Long userId);

    /**
     * 批量查询主播信息
     */
    List<AnchorInfoDto> queryAnchor();

}
