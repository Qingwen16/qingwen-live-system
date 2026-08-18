package com.wen.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wen.common.enums.DeleteEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 直播间信息表
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
@TableName("room_entity")
@Data
public class RoomEntity {

    /**
     * 直播间 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 推流名（唯一标识，腾讯云 StreamName）
     */
    private String streamName;

    /**
     * 直播间标题
     */
    private String title;

    /**
     * 当前观看人数
     */
    private Integer currentViewers;

    /**
     * 累计直播时长(小时)
     */
    private Long totalLiveHours;

    /**
     * 累计收益
     */
    private BigDecimal totalIncome;

    /**
     * 直播状态 0-未开播 1-直播中 2-回放 3-关闭
     */
    private Integer status;

    /**
     * 推流地址
     */
    private String streamUrl;

    /**
     * 拉流地址
     */
    private String playUrl;

    /**
     * 开始直播时间
     */
    private Long startTime;

    /**
     * 结束直播时间
     */
    private Long endTime;

    /**
     * 是否删除 0-已删除 1-正常
     */
    private Integer deleted = DeleteEnum.ACTIVE.getCode();

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}
