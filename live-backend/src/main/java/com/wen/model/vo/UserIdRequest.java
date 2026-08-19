package com.wen.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-08-19
 */
@Data
public class UserIdRequest {

    @NotNull(message = "请求用户ID不能为空")
    private Long userId;

}
