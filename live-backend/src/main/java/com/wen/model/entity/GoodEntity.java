package com.wen.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wen.common.enums.DeleteEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 货物信息实体类
 * @author : rjw
 * @date : 2026-04-08
 */
@Data
@TableName("good_entity")
public class GoodEntity {
    /**
     * 货物ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 货物代码
     */
    private String code;
    /**
     * 货物名称
     */
    private String name;
    /**
     * 货物描述
     */
    private String desc;
    /**
     * 货物价格
     */
    private BigDecimal price;
    /**
     * 货物单位
     */
    private String unit;
    /**
     * 货物重量(kg)
     */
    private BigDecimal weight;
    /**
     * 货物图片URL
     */
    private String imageUrl;
    /**
     * 货物状态：0-已下架，1-已上架，2-缺货
     */
    private Integer status;
    /**
     * 库存数量
     */
    private Integer stockCount;
    /**
     * 销售数量
     */
    private Integer salesCount;
    /**
     * 关联直播间 ID（可空，为空表示未挂载到直播间）
     */
    private Long roomId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否删除 0-已删除 1-正常
     */
    private Integer deleted = DeleteEnum.ACTIVE.getCode();
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
}
