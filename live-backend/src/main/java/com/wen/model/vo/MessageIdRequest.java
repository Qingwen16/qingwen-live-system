package com.wen.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 删除弹幕请求
 *
 * @author : rjw
 */
@Data
public class MessageIdRequest {

    /**
     * 消息 ID
     */
    @NotNull(message = "消息ID不能为空")
    private Long messageId;

}
