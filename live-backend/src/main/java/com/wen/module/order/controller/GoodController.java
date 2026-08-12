package com.wen.module.order.controller;

import com.wen.common.response.Response;
import com.wen.module.order.domain.vo.GoodCreateRequest;
import com.wen.module.order.domain.vo.GoodInfoDto;
import com.wen.module.order.domain.vo.GoodUpdateRequest;
import com.wen.module.order.service.GoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@RestController
@RequestMapping("/good")
@RequiredArgsConstructor
public class GoodController {

    private final GoodService goodService;

    /**
     * 新增商品
     */
    @PostMapping("/create/good")
    public Response<String> createGood(@RequestBody GoodCreateRequest request) {
        String response = goodService.createGood(request);
        return Response.success(response);
    }

    /**
     * 新增商品
     */
    @PostMapping("/update/good")
    public Response<String> updateGood(@RequestBody GoodUpdateRequest request) {
        String response = goodService.updateGood(request);
        return Response.success(response);
    }

    /**
     * 删除商品
     */
    @GetMapping("/delete/good")
    public Response<String> deleteGood(@Param("goodId") Long goodId) {
        String response = goodService.deleteGood(goodId);
        return Response.success(response);
    }

    /**
     * 获取所有可购买商品列表
     */
    @GetMapping("/queryTotalGoods")
    public Response<List<GoodInfoDto>> queryTotalGoods() {
        List<GoodInfoDto> goods = goodService.queryTotalGoods();
        return Response.success(goods);
    }

    /**
     * 获取所有上架商品列表
     */
    @GetMapping("/queryTotalListedGoods")
    public Response<List<GoodInfoDto>> queryTotalListedGoods() {
        List<GoodInfoDto> goods = goodService.queryTotalListedGoods();
        return Response.success(goods);
    }

    /**
     * 扣减库存
     */
    @GetMapping("/reduceGoodStock")
    public Response<String> reduceGoodStock(@Param("goodId") Long goodId, @Param("quantity") Integer quantity) {
        boolean response = goodService.reduceGoodStock(goodId, quantity);
        if (response) {
            return Response.success("货物库存减除成功");
        } else {
            return Response.success("货物库存减除失败");
        }
    }

}
