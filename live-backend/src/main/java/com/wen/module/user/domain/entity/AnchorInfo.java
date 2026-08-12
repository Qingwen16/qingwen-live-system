package com.wen.module.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@TableName("anchor_info")
@Data
public class AnchorInfo {
    /**
     * 主键 ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户ID
     */
    private String phone;

    /**
     * 主播昵称
     */
    private String nickname;

    /**
     * 封面图URL
     */
    private String coverUrl;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 直播间号
     */
    private String roomId;

    /**
     * 累计直播时长(小时)
     */
    private Long totalLiveHours;

    /**
     * 累计收益
     */
    private BigDecimal totalIncome;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 主播状态(0-正常 1-禁用 2-封禁)
     */
    private Integer status;

    /**
     * 是否删除(0-否 1-是)
     */
    private Integer deleted;

}
