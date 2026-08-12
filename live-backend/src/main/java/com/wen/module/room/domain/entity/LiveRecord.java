package com.wen.module.room.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 直播记录表
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
@TableName("live_record")
@Data
public class LiveRecord {

    /**
     * 记录 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 直播间 ID
     */
    private Long roomId;

    /**
     * 主播 ID
     */
    private Long anchorId;

    /**
     * 直播标题
     */
    private String title;

    /**
     * 直播封面
     */
    private String coverImage;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 直播时长（秒）
     */
    private Long duration;

    /**
     * 最高观看人数
     */
    private Integer peakViewers;

    /**
     * 累计观看人数
     */
    private Long totalViewers;

    /**
     * 收到礼物价值
     */
    private Long totalGiftValue;

    /**
     * 弹幕数量
     */
    private Integer messageCount;

    /**
     * 点赞数量
     */
    private Long likeCount;

    /**
     * 回放视频 URL
     */
    private String recordVideoUrl;

    /**
     * 回放状态 0-未生成 1-已生成 2-转码中
     */
    private Integer recordStatus;

    /**
     * 创建时间
     */
    private Long createTime;
}
