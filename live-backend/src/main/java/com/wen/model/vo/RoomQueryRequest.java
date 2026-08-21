package com.wen.model.vo;

import com.wen.common.enums.RoomStatusEnum;
import lombok.Data;

/**
 * 直播间列表查询请求
 *
 * @author jwruan
 */
@Data
public class RoomQueryRequest {

    /**
     * 直播间ID
     */
    private Long roomId;

    /**
     * 直播间标题（模糊匹配）
     */
    private String title;
}
