package com.wen.model.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色查询请求
 *
 * @author : rjw
 */
@Data
public class RoleQueryRequest {

    /**
     * 页码，从 1 开始
     */
    @NotNull
    @Min(value = 1, message = "获取页码最小页为1")
    private long pageNum;

    /**
     * 每页大小
     */
    @NotNull
    @Min(value = 1, message = "获取条数最小为1")
    private long pageSize;

    /**
     * 角色类型列表 {@link com.wen.common.enums.RoleTypeEnum}
     */
    private List<Integer> types;

}
