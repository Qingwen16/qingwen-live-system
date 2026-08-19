package com.wen.model.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 直播间聊天历史查询请求
 *
 * @author : rjw
 */
@Data
public class MessageGetRequest {

    /**
     * 直播间 ID
     */
    @NotNull
    private Long roomId;

    /**
     * 拉取条数，默认 100
     */
    @NotNull
    @Min(value = 1, message = "拉取条数不能小于1")
    @Max(value = 200, message = "拉取条数不能大于200")
    private Integer limit = 100;

}
