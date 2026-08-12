package com.wen.module.order.domain.vo;

import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class GoodQueryRequest {

    /**
     * 货物编号
     */
    private String number;

    /**
     * 货物名称
     */
    private String name;

}
