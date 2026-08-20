package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.Response;
import com.wen.model.dto.RoomDto;
import com.wen.model.vo.RoomIdRequest;
import com.wen.model.vo.RoomOnlineCountVo;
import com.wen.model.vo.RoomGetRequest;
import com.wen.model.vo.RoomRequest;
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
     * 创建直播间（一个主播只能创建一个）
     */
    @PostMapping("/create")
    @RequireRole({RoleTypeEnum.ANCHOR, RoleTypeEnum.SUPER_ADMIN})
    public Response<RoomDto> createRoom(@Valid @RequestBody RoomRequest request) {
        return Response.success(roomService.createRoom(request));
    }

    /**
     * 修改直播间
     */
    @PostMapping("/update")
    @RequireRole({RoleTypeEnum.ANCHOR, RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> updateRoom(@Valid @RequestBody RoomRequest request) {
        roomService.updateRoom(request);
        return Response.success(null, "直播间修改完成");
    }

    /**
     * 删除直播间
     */
    @PostMapping("/delete")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN, RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> delete(@RequestBody RoomRequest request) {
        roomService.deleteRoom(request);
        return Response.success(null, "直播间已删除");
    }

    /**
     * 开启直播间（主播开启自己的直播间，管理员可开启任意直播间）
     */
    @PostMapping("/open")
    @RequireRole({RoleTypeEnum.ANCHOR, RoleTypeEnum.SUPER_ADMIN, RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> openRoom(@RequestBody RoomIdRequest request) {
        roomService.openRoom(request);
        return Response.success(null, "直播间已开启");
    }

    /**
     * 关闭直播间（主播关闭自己的直播间，管理员可关闭任意直播间）
     */
    @PostMapping("/close")
    @RequireRole({RoleTypeEnum.ANCHOR, RoleTypeEnum.ADMIN, RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> closeRoom(@RequestBody RoomIdRequest request) {
        roomService.closeRoom(request);
        return Response.success(null, "直播间已关闭");
    }

    /**
     * APP查询直播间列表
     */
    @PostMapping("/getRoomList")
    public Response<List<RoomDto>> getRoomList(@RequestBody RoomGetRequest request) {
        return Response.success(roomService.getRoomList(request));
    }

    /**
     * 查询单个直播间信息
     */
    @PostMapping("/getRoomInfo")
    public Response<RoomDto> getRoomInfo(@RequestBody RoomIdRequest request) {
        return Response.success(roomService.getRoomInfo(request));
    }

    /**
     * 查询所有直播间在线人数（前端轮询，轻量数据）
     */
    @PostMapping("/onlineCounts")
    public Response<List<RoomOnlineCountVo>> onlineCounts() {
        return Response.success(roomService.getOnlineCounts());
    }
}
