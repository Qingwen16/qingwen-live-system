package com.wen.model.vo;

import lombok.Data;

/**
 * 直播间创建 / 更新请求
 *
 * @author jwruan
 */
@Data
public class RoomRequest {

    /**
     * 直播间 ID（更新时使用）
     */
    private Long id;

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
     * 直播标签（JSON 数组字符串）
     */
    private String tags;
}
