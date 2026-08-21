package com.wen.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单 ID 请求
 *
 * @author jwruan
 */
@Data
public class OrderIdRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

}
