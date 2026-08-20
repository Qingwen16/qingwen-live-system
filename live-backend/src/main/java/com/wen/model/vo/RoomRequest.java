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
public class RoomRequest {

    /**
     * 直播间 ID（更新时使用）
     */
    @NotNull(message = "直播间ID不能为空")
    private Long roomId;

    /**
     * 直播间标题
     */
    @NotBlank(message = "直播间标题不能为空")
    private String title;
}
