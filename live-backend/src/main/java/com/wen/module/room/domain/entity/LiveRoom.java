package com.wen.module.room.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 直播间信息表
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
@TableName("live_room")
@Data
public class LiveRoom {

    /**
     * 直播间 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 房间号（唯一标识）
     */
    private String roomNumber;

    /**
     * 主播 ID（关联 AnchorInfo）
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
     * 分区 ID（关联 LiveCategory）
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
     * 直播状态 0-未开播 1-直播中 2-回放 3-关闭
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

