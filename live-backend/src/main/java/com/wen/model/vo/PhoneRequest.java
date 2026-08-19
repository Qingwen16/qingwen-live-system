package com.wen.model.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-08-19
 */
@Data
public class PhoneRequest {

    @NotBlank(message = "请求电话不能为空")
    private String phone;

}
