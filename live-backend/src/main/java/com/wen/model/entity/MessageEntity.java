package com.wen.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wen.common.enums.DeleteEnum;
import lombok.Data;

/**
 * 直播间聊天消息实体
 *
 * @author : rjw
 */
@TableName("message_entity")
@Data
public class MessageEntity {

    /**
     * 主键 ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
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
     * 发言用户名（冗余，展示用）
     */
    private String username;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否删除 0-已删除 1-正常
     */
    private Integer deleted = DeleteEnum.ACTIVE.getCode();

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

}
