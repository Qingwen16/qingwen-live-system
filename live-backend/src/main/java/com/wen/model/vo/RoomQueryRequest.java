package com.wen.model.vo;

import lombok.Data;

/**
 * 直播间列表查询请求
 *
 * @author jwruan
 */
@Data
public class RoomQueryRequest {

    /**
     * 直播间标题（模糊匹配）
     */
    private String title;

    /**
     * 直播状态 {@link com.wen.common.enums.RoomStatus}
     */
    private Integer status;

    /**
     * 分区 ID
     */
    private Long categoryId;

    /**
     * 是否推荐 0-否 1-是
     */
    private Integer isRecommend;

    /**
     * 当前页码，从 1 开始
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}
