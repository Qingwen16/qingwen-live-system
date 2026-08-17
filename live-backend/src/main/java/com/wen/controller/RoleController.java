package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.Response;
import com.wen.model.dto.RoleDto;
import com.wen.model.vo.RoleQueryRequest;
import com.wen.model.vo.RoleSetRequest;
import com.wen.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequireRole({RoleTypeEnum.SUPER_ADMIN})
public class RoleController {

    private final RoleService roleService;

    /**
     * 查询用户角色
     */
    @PostMapping("/query")
    public Response<List<RoleDto>> queryRole(@RequestBody RoleQueryRequest request) {
        List<RoleDto> response = roleService.queryRole(request.getTypes());
        return Response.success(response);
    }

    /**
     * 设置用户角色
     */
    @PostMapping("/set")
    public Response<String> setRole(@RequestBody RoleSetRequest request) {
        String response = roleService.setRole(request.getPhone(), request.getRole());
        return Response.success(response);
    }

}
