package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.Response;
import com.wen.model.dto.GoodDto;
import com.wen.model.vo.*;
import com.wen.service.GoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 * 后台商品管理仅超级管理员可操作，直播间商品列表供登录用户查看
 *
 * @author : rjw
 */
@RestController
@RequestMapping("/good")
@RequiredArgsConstructor
public class GoodController {

    private final GoodService goodService;

    /**
     * 新增商品（默认未上架）
     */
    @PostMapping("/create")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<Long> createGood(@RequestBody GoodCreateRequest request) {
        return Response.success(goodService.createGood(request), "新增商品成功");
    }

    /**
     * 修改商品
     */
    @PostMapping("/update")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> updateGood(@RequestBody GoodUpdateRequest request) {
        goodService.updateGood(request);
        return Response.success(null, "更新商品成功");
    }

    /**
     * 删除商品（软删除）
     */
    @PostMapping("/delete")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> deleteGood(@RequestBody GoodIdRequest request) {
        goodService.deleteGood(request);
        return Response.success(null, "删除商品成功");
    }

    /**
     * 查询商品列表（全量，支持筛选）
     */
    @PostMapping("/list")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<List<GoodDto>> queryGoods(@RequestBody GoodQueryRequest request) {
        return Response.success(goodService.queryGoods(request));
    }

    /**
     * 查询商品列表，直播手机端展示，只需获取全量上架商品
     */
    @PostMapping("/app/list")
    public Response<List<GoodDto>> queryAppGoods() {
        return Response.success(goodService.queryAppGoods());
    }

    /**
     * 查询直播间已上架商品（用户端，登录即可访问）
     */
    @PostMapping("/room")
    public Response<List<GoodDto>> queryRoomGoods(@RequestBody RoomIdRequest request) {
        return Response.success(goodService.queryRoomGoods(request));
    }

    /**
     * 上架商品
     */
    @PostMapping("/on-shelf")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> onShelf(@RequestBody GoodIdRequest request) {
        goodService.onShelf(request);
        return Response.success(null, "上架成功");
    }

    /**
     * 下架商品
     */
    @PostMapping("/off-shelf")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> offShelf(@RequestBody GoodIdRequest request) {
        goodService.offShelf(request);
        return Response.success(null, "下架成功");
    }

    /**
     * 挂载商品到当前主播直播间（最多 3 个）
     */
    @PostMapping("/mount")
    @RequireRole({RoleTypeEnum.ANCHOR})
    public Response<Void> mountToRoom(@RequestBody GoodIdRequest request) {
        goodService.mountToRoom(request);
        return Response.success(null, "挂载成功");
    }

    /**
     * 从当前主播直播间移除商品
     */
    @PostMapping("/unmount")
    @RequireRole({RoleTypeEnum.ANCHOR})
    public Response<Void> unmountFromRoom(@RequestBody GoodIdRequest request) {
        goodService.unmountFromRoom(request);
        return Response.success(null, "移除成功");
    }

}
