package com.wen.model.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 后台管理分页查询直播间消息请求（超管专用，可查已删除）
 *
 * @author : rjw
 */
@Data
public class MessageGetWebRequest {

    /**
     * 页码，从 1 开始
     */
    @NotNull
    @Min(value = 1, message = "获取页码最小页为1")
    private long pageNum;

    /**
     * 每页大小
     */
    @NotNull
    @Min(value = 1, message = "获取条数最小为1")
    private long pageSize;

    /**
     * 直播间 ID（可选，不传则查全部）
     */
    private Long roomId;

    /**
     * 删除状态（可选：0-已删除 1-正常，不传则查全部）
     */
    private Integer deleted;

}
