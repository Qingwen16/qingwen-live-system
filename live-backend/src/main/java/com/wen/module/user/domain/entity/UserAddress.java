package com.wen.module.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户收货地址实体类
 * @author : rjw
 * @date : 2026-04-08
 */
@Data
@TableName("user_address")
public class UserAddress {
    /**
     * 地址ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 收货人姓名
     */
    private String name;
    /**
     * 收货人电话
     */
    private String phone;
    /**
     * 国家
     */
    private String country;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 完整地址（省+市+区+详细地址）
     */
    private String fullAddress;
    /**
     * 邮政编码
     */
    private String postalCode;
    /**
     * 地址标签：1-家，2-公司，3-学校，4-其他
     */
    private Integer zipCode;
    /**
     * 是否默认地址：0-否，1-是
     */
    private Integer isDefault;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
}