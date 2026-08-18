package com.wen.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wen.common.enums.DeleteEnum;
import lombok.Data;

/**
 * 主播-直播间关联表（多对多）
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
@TableName("user_room")
public class UserRoom {

    /**
     * 主键 ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主播用户 ID（关联 UserEntity.userId）
     */
    private Long userId;

    /**
     * 直播间 ID（关联 RoomEntity.id）
     */
    private Long roomId;

    /**
     * 是否删除 0-已删除 1-正常
     */
    private Integer deleted = DeleteEnum.ACTIVE.getCode();

}
