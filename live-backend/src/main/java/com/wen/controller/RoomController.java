package com.wen.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.Response;
import com.wen.model.dto.RoomDto;
import com.wen.model.vo.RoomQueryRequest;
import com.wen.model.vo.RoomRequest;
import com.wen.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/createRoom")
    @RequireRole({RoleTypeEnum.ANCHOR})
    public Response<RoomDto> createRoom(@RequestBody RoomRequest request) {
        return Response.success(roomService.createRoom(request));
    }

    /**
     * 修改直播间
     */
    @PostMapping("/updateRoom")
    @RequireRole({RoleTypeEnum.ANCHOR})
    public Response<RoomDto> updateRoom(@RequestBody RoomRequest request) {
        return Response.success(roomService.updateRoom(request));
    }

    /**
     * 关闭直播间（主播关闭自己的直播间，管理员可关闭任意直播间）
     */
    @PostMapping("/closeRoom")
    @RequireRole({RoleTypeEnum.ANCHOR, RoleTypeEnum.ADMIN})
    public Response<Void> closeRoom(@RequestParam("roomId") Long roomId) {
        roomService.closeRoom(roomId);
        return Response.success(null, "直播间已关闭");
    }

    /**
     * 分页查询直播间列表
     */
    @PostMapping("/getRoomList")
    public Response<IPage<RoomDto>> getRoomList(@RequestBody RoomQueryRequest request) {
        return Response.success(roomService.getRoomList(request));
    }

    /**
     * 查询单个直播间信息
     */
    @PostMapping("/getRoomInfo")
    public Response<RoomDto> getRoomInfo(@RequestParam("roomId") Long roomId) {
        return Response.success(roomService.getRoomInfo(roomId));
    }
}
