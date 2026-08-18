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
}
