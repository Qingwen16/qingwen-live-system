package com.wen.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 直播间创建 / 更新请求
 *
 * @author jwruan
 */
@Data
public class RoomCreateRequest {

    /**
     * 主播 ID
     */
    @NotNull(message = "主播ID不能为空")
    private Long userId;

    /**
     * 直播间ID是否随机
     */
    @NotNull(message = "需要确认是否使用随机直播间ID")
    private Integer isRandom;

    /**
     * 直播间 ID
     */
    private Long roomId;

    /**
     * 直播间标题
     */
    @NotBlank(message = "直播间标题不能为空")
    private String title;
}
