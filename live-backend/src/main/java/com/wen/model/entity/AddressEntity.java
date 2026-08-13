package com.wen.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户收货地址实体类
 *
 * @author : rjw
 * @date : 2026-04-08
 */
@Data
@TableName("address_entity")
public class AddressEntity {

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
     * 区/县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 地址标签：1-家，2-公司，3-学校，4-其他
     */
    private Integer tag;

    /**
     * 是否默认地址：0-否，1-是
     */
    private Integer isDefault;

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

    /**
     * 完整地址（省+市+区+详细地址），动态计算，不落库
     */
    public String getFullAddress() {
        StringBuilder builder = new StringBuilder();
        if (province != null) {
            builder.append(province);
        }
        if (city != null) {
            builder.append(city);
        }
        if (district != null) {
            builder.append(district);
        }
        if (address != null) {
            builder.append(address);
        }
        return builder.toString();
    }
}
