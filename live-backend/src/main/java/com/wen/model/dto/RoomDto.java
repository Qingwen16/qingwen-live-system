package com.wen.model.dto;

import lombok.Data;

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
     * 房间号（唯一标识）
     */
    private String roomNumber;

    /**
     * 主播 ID
     */
    private Long anchorId;

    /**
     * 直播间标题
     */
    private String title;

    /**
     * 直播间封面图
     */
    private String coverImage;

    /**
     * 分区 ID
     */
    private Long categoryId;

    /**
     * 直播间公告
     */
    private String announcement;

    /**
     * 直播标签（JSON 数组）
     */
    private String tags;

    /**
     * 当前观看人数
     */
    private Integer currentViewers;

    /**
     * 累计观看人数
     */
    private Long totalViewers;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 关注数
     */
    private Long followCount;

    /**
     * 直播状态 {@link com.wen.common.enums.RoomStatus}
     */
    private Integer status;

    /**
     * 是否推荐 0-否 1-是
     */
    private Integer isRecommend;

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
