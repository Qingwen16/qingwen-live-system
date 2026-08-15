package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.PageResult;
import com.wen.common.response.Response;
import com.wen.model.entity.AddressEntity;
import com.wen.model.vo.AddressIdRequest;
import com.wen.model.vo.AddressQueryRequest;
import com.wen.model.vo.AddressRequest;
import com.wen.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地址控制器
 * 用户端操作自己的地址（登录即可），管理端分页/强制删除（方法级角色控制）
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // ===== 用户端：操作自己的地址（登录即可） =====

    /**
     * 查询当前登录用户的收货地址
     */
    @PostMapping("/query")
    public Response<List<AddressEntity>> queryAddress() {
        return Response.success(addressService.queryAddress());
    }

    /**
     * 新增收货地址
     */
    @PostMapping("/create")
    public Response<Void> createAddress(@RequestBody AddressRequest request) {
        addressService.createAddress(request);
        return Response.success(null, "地址已保存");
    }

    /**
     * 修改收货地址
     */
    @PostMapping("/update")
    public Response<Void> updateAddress(@RequestBody AddressRequest request) {
        addressService.updateAddress(request);
        return Response.success(null, "地址已更新");
    }

    /**
     * 删除自己的收货地址
     */
    @PostMapping("/delete")
    public Response<Void> deleteAddress(@RequestBody AddressIdRequest request) {
        addressService.deleteAddress(request);
        return Response.success(null, "地址已删除");
    }

    // ===== 管理端：管理所有地址（方法级角色控制） =====

    /**
     * 分页查询地址
     */
    @PostMapping("/web/query")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<PageResult<AddressEntity>> webQueryAddress(@RequestBody AddressQueryRequest request) {
        return Response.success(addressService.webQueryAddress(request));
    }

    /**
     * 强制删除地址
     */
    @PostMapping("/web/delete")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> webDeleteAddress(@RequestBody AddressIdRequest request) {
        addressService.webDeleteAddress(request);
        return Response.success(null, "地址已删除");
    }
}
