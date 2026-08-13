package com.wen.controller;

import com.wen.common.response.Response;
import com.wen.model.vo.AddressRequest;
import com.wen.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * 查询当前登录用户的收货地址
     */
    @GetMapping("/query")
    public Response<?> queryUserAddress() {
        return Response.success(addressService.queryUserAddress());
    }

    /**
     * 新增收货地址
     */
    @PostMapping("/create")
    public Response<?> createUserAddress(@RequestBody AddressRequest request) {
        addressService.createUserAddress(request);
        return Response.success();
    }

    /**
     * 修改收货地址
     */
    @PostMapping("/update")
    public Response<?> updateUserAddress(@RequestBody AddressRequest request) {
        addressService.updateUserAddress(request);
        return Response.success();
    }

    /**
     * 删除收货地址
     */
    @PostMapping("/delete")
    public Response<?> deleteUserAddress(@RequestParam Long id) {
        addressService.deleteUserAddress(id);
        return Response.success();
    }
}
