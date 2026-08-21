package com.wen.service;

import com.wen.model.dto.RoomDto;
import com.wen.model.vo.*;

import java.util.List;

/**
 * 直播间服务接口
 *
 * @author jwruan
 */
public interface RoomService {

    /**
     * 创建直播间（一个主播只能创建一个直播间）
     */
    void createRoom(RoomCreateRequest request);

    /**
     * 更新直播间信息（主播只能更新自己的直播间）
     */
    void updateRoom(RoomUpdateRequest request);

    /**
     * 删除直播间（主播只能删除自己的直播间）
     */
    void deleteRoom(RoomIdRequest request);

    /**
     * 开启直播间（主播开启自己的直播间，管理员可开启任意直播间）
     */
    void openRoom(RoomIdRequest request);

    /**
     * 关闭直播间（主播关闭自己的直播间，管理员可关闭任意直播间）
     */
    void closeRoom(RoomIdRequest request);

    /**
     * 查询直播间列表（全量）
     */
    List<RoomDto> queryRoomList(RoomQueryRequest request);

    /**
     * Web查询直播间列表（全量）
     */
    List<RoomDto> queryRoomListWeb(RoomQueryWebRequest request);

}
