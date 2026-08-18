package com.wen.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-08-14
 */
@Data
public class AddressIdRequest {

    @NotNull(message = "请求的地址ID不能为空")
    private Long addressId;

}
