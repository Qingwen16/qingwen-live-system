package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.PageResult;
import com.wen.common.response.Response;
import com.wen.model.dto.UserDto;
import com.wen.model.vo.PhoneRequest;
import com.wen.model.vo.UserIdRequest;
import com.wen.model.vo.UserGetRequest;
import com.wen.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Response<PageResult<UserDto>> queryByCondition(@Valid @RequestBody UserGetRequest request) {
        PageResult<UserDto> response = userService.queryByCondition(request);
        return Response.success(response);
    }

    /**
     * 根据手机号查询用户
     */
    @PostMapping("/queryByPhone")
    public Response<UserDto> queryByPhone(@Valid @RequestBody PhoneRequest request) {
        UserDto response = userService.queryByPhone(request);
        return Response.success(response);
    }

    /**
     * 禁用用户
     */
    @PostMapping("/disable")
    public Response<Void> disableUser(@Valid @RequestBody UserIdRequest request) {
        userService.disableUser(request);
        return Response.success(null, "用户已禁用");
    }

    /**
     * 启用用户
     */
    @PostMapping("/enable")
    public Response<Void> enableUser(@Valid @RequestBody UserIdRequest request) {
        userService.enableUser(request);
        return Response.success(null, "用户已启用");
    }

    /**
     * 注销用户（软删除）
     */
    @PostMapping("/delete")
    public Response<Void> deleteUser(@Valid @RequestBody UserIdRequest request) {
        userService.deleteUser(request);
        return Response.success(null, "用户已注销");
    }

}
