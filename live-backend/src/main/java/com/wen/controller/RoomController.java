package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.Response;
import com.wen.model.dto.RoomDto;
import com.wen.model.vo.*;
import com.wen.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 直播间控制器
 *
 * @author jwruan
 */
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * 创建直播间，只有超级管理员可以创建
     */
    @PostMapping("/create")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<RoomDto> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        roomService.createRoom(request);
        return Response.success(null, request.getRoomId() + " - 直播间创建完成");
    }

    /**
     * 修改直播间，只能本直播间的主播和超级管理员可以修改
     */
    @PostMapping("/update")
    @RequireRole({RoleTypeEnum.ANCHOR, RoleTypeEnum.ADMIN, RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> updateRoom(@Valid @RequestBody RoomUpdateRequest request) {
        roomService.updateRoom(request);
        return Response.success(null, request.getRoomId() + " - 直播间修改完成");
    }

    /**
     * 删除直播间，只有超级管理员可以设置
     */
    @PostMapping("/delete")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> delete(@RequestBody RoomIdRequest request) {
        roomService.deleteRoom(request);
        return Response.success(null, request.getRoomId() + " - 直播间已删除");
    }

    /**
     * 开启直播间（主播开启自己的直播间，管理员可开启任意直播间）
     */
    @PostMapping("/open")
    @RequireRole({RoleTypeEnum.ANCHOR, RoleTypeEnum.ADMIN, RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> openRoom(@RequestBody RoomIdRequest request) {
        roomService.openRoom(request);
        return Response.success(null, request.getRoomId() + " - 直播间已开启");
    }

    /**
     * 关闭直播间（主播关闭自己的直播间，管理员可关闭任意直播间）
     */
    @PostMapping("/close")
    @RequireRole({RoleTypeEnum.ANCHOR, RoleTypeEnum.ADMIN, RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> closeRoom(@RequestBody RoomIdRequest request) {
        roomService.closeRoom(request);
        return Response.success(null, request.getRoomId() + " - 直播间已关闭");
    }

    /**
     * APP查询直播间列表
     */
    @PostMapping("/queryRoomList")
    public Response<List<RoomDto>> queryRoomList(@RequestBody RoomQueryRequest request) {
        return Response.success(roomService.queryRoomList(request));
    }

    /**
     * APP查询直播间列表
     */
    @PostMapping("/web/queryRoomList")
    public Response<List<RoomDto>> queryRoomListWeb(@RequestBody RoomQueryWebRequest request) {
        return Response.success(roomService.queryRoomListWeb(request));
    }

}
