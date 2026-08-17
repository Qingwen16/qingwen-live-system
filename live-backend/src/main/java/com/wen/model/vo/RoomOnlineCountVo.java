package com.wen.model.vo;

import lombok.Data;

/**
 * 直播间在线人数（轻量轮询响应，仅返回房间 ID + 在线人数）
 *
 * @author jwruan
 */
@Data
public class RoomOnlineCountVo {

    /**
     * 直播间 ID
     */
    private Long roomId;

    /**
     * 当前在线人数
     */
    private Integer currentViewers;
}
