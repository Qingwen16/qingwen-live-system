package com.wen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wen.model.dto.RoomDto;
import com.wen.model.vo.RoomQueryRequest;
import com.wen.model.vo.RoomRequest;

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
     * 关闭直播间（主播关闭自己的直播间，管理员可关闭任意直播间）
     */
    void closeRoom(Long roomId);

    /**
     * 分页查询直播间列表
     */
    IPage<RoomDto> getRoomList(RoomQueryRequest request);

    /**
     * 查询直播间详情
     */
    RoomDto getRoomInfo(Long roomId);
}
