package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.Response;
import com.wen.model.dto.UserDto;
import com.wen.model.vo.UserQueryRequest;
import com.wen.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户信息控制器
 *
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@RequireRole({RoleTypeEnum.ADMIN, RoleTypeEnum.SUPER_ADMIN})
public class UserController {

    private final UserService userService;

    /**
     * 根据条件查询用户列表
     */
    @PostMapping("/queryByCondition")
    public Response<List<UserDto>> queryByCondition(@RequestBody UserQueryRequest request) {
        List<UserDto> response = userService.queryByCondition(request);
        return Response.success(response);
    }

    /**
     * 根据手机号查询用户
     */
    @GetMapping("/queryByPhone")
    public Response<UserDto> queryByPhone(@RequestParam("phone") String phone) {
        UserDto response = userService.queryByPhone(phone);
        return Response.success(response);
    }

    /**
     * 禁用用户
     */
    @PostMapping("/disable")
    public Response<Void> disableUser(@RequestParam("userId") Long userId) {
        userService.disableUser(userId);
        return Response.success(null, "用户已禁用");
    }

    /**
     * 启用用户
     */
    @PostMapping("/enable")
    public Response<Void> enableUser(@RequestParam("userId") Long userId) {
        userService.enableUser(userId);
        return Response.success(null, "用户已启用");
    }

    /**
     * 注销用户（软删除）
     */
    @PostMapping("/delete")
    public Response<Void> deleteUser(@RequestParam("userId") Long userId) {
        userService.deleteUser(userId);
        return Response.success(null, "用户已注销");
    }

}
