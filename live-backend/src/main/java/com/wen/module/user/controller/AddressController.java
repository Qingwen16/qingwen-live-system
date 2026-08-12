package com.wen.module.user.controller;

import com.wen.common.response.Response;
import com.wen.module.user.domain.vo.AddressRequest;
import com.wen.module.user.service.AddressService;
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
     * 查询用户收货地址
     */
    @GetMapping("/query")
    public Response<?> queryUserAddress(@RequestParam Long userId) {
        return Response.success(addressService.queryUserAddress(userId));
    }
    /**
     * 新增用户收货地址
     */
    @PostMapping("/create")
    public Response<?> createUserAddress(@RequestBody AddressRequest request) {
        addressService.createUserAddress(request);
        return Response.success();
    }
    /**
     * 修改用户收货地址
     */
    @PostMapping("/update")
    public Response<?> updateUserAddress(@RequestBody AddressRequest request) {
        addressService.updateUserAddress(request);
        return Response.success();
    }
    /**
     * 删除用户收货地址
     */
    @PostMapping("/delete")
    public Response<?> deleteUserAddress(@RequestParam Long id, @RequestParam Long userId) {
        addressService.deleteUserAddress(id, userId);
        return Response.success();
    }
}