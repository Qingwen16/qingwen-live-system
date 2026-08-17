package com.wen.model.vo;

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
     * 角色类型列表 {@link com.wen.common.enums.RoleTypeEnum}
     */
    private List<Integer> types;

}
