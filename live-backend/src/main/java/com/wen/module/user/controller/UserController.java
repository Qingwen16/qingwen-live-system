package com.wen.module.user.controller;

import com.wen.common.response.Response;
import com.wen.module.user.domain.vo.UserInfoDto;
import com.wen.module.user.domain.vo.UserQueryRequest;
import com.wen.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
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
public class UserController {

    private UserService userService;

    @PostMapping("/queryUserByCondition")
    public Response<List<UserInfoDto>> queryUserByCondition(@RequestBody UserQueryRequest request) {
        List<UserInfoDto> response = userService.queryUserByCondition(request);
        return Response.success(response);
    }

    @GetMapping("/queryByPhone")
    public Response<UserInfoDto> queryByPhone(@Param("phone") String phone) {
        UserInfoDto response = userService.queryByPhone(phone);
        return Response.success(response);
    }


}
