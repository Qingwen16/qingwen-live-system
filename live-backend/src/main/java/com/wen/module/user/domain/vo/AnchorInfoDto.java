package com.wen.module.user.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Data
public class AnchorInfoDto {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 主播昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别(0-女 1-男 2-未知)
     */
    private Integer gender;

    /**
     * 头像URL
     */
    private String avatarUrl;

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
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 详细地址
     */
    private String address;

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
