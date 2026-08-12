package com.wen.module.user.controller;

import com.wen.common.response.Response;
import com.wen.module.user.domain.vo.UserRoleDto;
import com.wen.module.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 查询用户角色
     */
    @GetMapping("/query")
    public Response<List<UserRoleDto>> queryRole(@Param("type") List<Integer> types) {
        List<UserRoleDto> response = roleService.queryRole(types);
        return Response.success(response);
    }

    /**
     * 设置超级管理员
     */
    @GetMapping("/set/admin")
    public Response<String> setAdminRole(@Param("phone") String phone, @Param("code") String code) {
        String response = roleService.setAdmin(phone, code);
        return Response.success(response);
    }

    /**
     * 设置超级管理员
     */
    @GetMapping("/set/roomAdmin")
    public Response<String> setRoomAdminRole(@Param("phone") String phone) {
        String response = roleService.setRoomAdminRole(phone);
        return Response.success(response);
    }

    /**
     * 设置超级管理员
     */
    @GetMapping("/set/anchor")
    public Response<String> setAnchorRole(@Param("phone") String phone) {
        String response = roleService.setAnchorRole(phone);
        return Response.success(response);
    }

    /**
     * 设置超级管理员
     */
    @GetMapping("/set/user")
    public Response<String> setUserRole(@Param("phone") String phone) {
        String response = roleService.setUserRole(phone);
        return Response.success(response);
    }

}
