package com.wen.model.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户收货地址请求参数
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class AddressRequest {

    /**
     * 地址ID（修改时必传）
     */
    private Long id;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String name;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    private String phone;

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * 区/县
     */
    @NotBlank(message = "区/县不能为空")
    private String district;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String address;

    /**
     * 是否默认地址：0-否，1-是
     */
    @NotNull(message = "是否默认不能为空")
    @Min(value = 0, message = "是否默认取值只能为0或1")
    @Max(value = 1, message = "是否默认取值只能为0或1")
    private Integer isDefault;

}
