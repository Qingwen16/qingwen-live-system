package com.wen.model.dto;

import com.wen.common.enums.RoomStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 直播间信息响应
 *
 * @author jwruan
 */
@Data
public class RoomDto {

    /**
     * 直播间 ID
     */
    private Long id;

    /**
     * 推流名（腾讯云 StreamName）
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
     * 直播状态 {@link RoomStatusEnum}
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
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;
}
