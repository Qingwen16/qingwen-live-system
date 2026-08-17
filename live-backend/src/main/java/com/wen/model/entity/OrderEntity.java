package com.wen.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wen.common.enums.DeleteEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单信息实体类
 *
 * 设计理念：订单保存收货地址「快照」，而非引用地址表。
 * 下单时将收货人、电话、完整地址复制进订单，订单与地址表彻底解耦，
 * 用户后续修改或删除地址簿不影响历史订单。
 * 订单本身是交易数据，保留逻辑删除（deleted）以便追溯。
 *
 * @author : rjw
 * @date : 2026-04-08
 */
@Data
@TableName("order_entity")
public class OrderEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long goodId;

    /**
     * 直播间 ID（下单时从商品反查，可空，商品未挂直播间时为 null）
     */
    private Long roomId;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已完成
     */
    private Integer status;

    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer payType;

    /**
     * 支付时间
     */
    private Long payTime;

    /**
     * 收货人姓名（下单时快照）
     */
    private String receiverName;

    /**
     * 收货人电话（下单时快照）
     */
    private String receiverPhone;

    /**
     * 收货完整地址（下单时快照，省+市+区+详细地址）
     */
    private String receiverAddress;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除：0-已删除，1-正常
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
