package com.wen.service;

import com.wen.model.dto.RoomDto;
import com.wen.model.vo.RoomIdRequest;
import com.wen.model.vo.RoomOnlineCountVo;
import com.wen.model.vo.RoomGetRequest;
import com.wen.model.vo.RoomRequest;

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
    RoomDto createRoom(RoomRequest request);

    /**
     * 更新直播间信息（主播只能更新自己的直播间）
     */
    RoomDto updateRoom(RoomRequest request);

    /**
     * 删除直播间（主播只能删除自己的直播间）
     */
    RoomDto deleteRoom(RoomRequest request);

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
    List<RoomDto> getRoomList(RoomGetRequest request);

    /**
     * 查询直播间详情
     */
    RoomDto getRoomInfo(RoomIdRequest request);

    /**
     * 进入直播间（在线人数 +1）
     */
    void incrementViewers(Long roomId);

    /**
     * 离开直播间（在线人数 -1）
     */
    void decrementViewers(Long roomId);

    /**
     * 查询所有直播间在线人数（轻量，仅返回 roomId + currentViewers，供前端轮询）
     */
    List<RoomOnlineCountVo> getOnlineCounts();
}
