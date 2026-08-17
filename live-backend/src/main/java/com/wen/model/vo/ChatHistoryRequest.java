package com.wen.model.vo;

import lombok.Data;

/**
 * 聊天历史查询请求
 *
 * @author : rjw
 */
@Data
public class ChatHistoryRequest {

    /**
     * 直播间 ID
     */
    private Long roomId;

    /**
     * 拉取条数，默认 50
     */
    private Integer limit = 50;

}
