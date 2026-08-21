package com.wen.model.vo;

import lombok.Data;

/**
 * 直播间列表查询请求
 *
 * @author jwruan
 */
@Data
public class RoomQueryWebRequest {

    /**
     * 直播间ID
     */
    private Long roomId;

    /**
     * 直播间标题（模糊匹配）
     */
    private String title;

    /**
     * 直播状态 {@link com.wen.common.enums.RoomStatusEnum}
     */
    private Integer status;

    /**
     * 是否删除 {@link com.wen.common.enums.DefaultEnum}
     */
    private Integer deleted;
}
