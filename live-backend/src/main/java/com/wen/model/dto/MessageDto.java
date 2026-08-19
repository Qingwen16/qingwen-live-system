package com.wen.model.dto;

import lombok.Data;

/**
 * 聊天消息返回对象（广播与历史查询共用）
 *
 * @author : rjw
 */
@Data
public class MessageDto {

    /**
     * 消息 ID（主键，删除/后台定位用）
     */
    private Long id;

    /**
     * 直播间 ID
     */
    private Long roomId;

    /**
     * 发言用户 ID
     */
    private Long userId;

    /**
     * 发言用户名
     */
    private String username;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否删除 0-已删除 1-正常（后台管理查询用）
     */
    private Integer deleted;

    /**
     * 发送时间（时间戳）
     */
    private Long createTime;

}
