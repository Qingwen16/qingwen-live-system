package com.wen.common.generator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wen.common.exception.BusinessException;
import com.wen.mapper.RoomMapper;
import com.wen.model.entity.RoomEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 直播间 ID 生成器：生成 6 位随机数字（100000~999999），查库校验唯一，冲突则重试
 *
 * @author : rjw
 * @date : 2026-08-20
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RoomIdGenerator {

    private static final int MIN_ROOM_ID = 100000;
    private static final int MAX_ROOM_ID = 999999;
    private static final int MAX_RETRY = 5;

    private final RoomMapper roomMapper;

    /**
     * 生成一个未被占用的 6 位直播间 ID
     */
    public Long generate() {
        for (int i = 0; i < MAX_RETRY; i++) {
            long roomId = ThreadLocalRandom.current().nextInt(MIN_ROOM_ID, MAX_ROOM_ID + 1);
            // 主键唯一性校验不能过滤软删除记录，否则会与已删除行占用同一个 ID
            Long count = roomMapper.selectCount(new LambdaQueryWrapper<RoomEntity>()
                    .eq(RoomEntity::getRoomId, roomId));
            if (count == null || count == 0) {
                return roomId;
            }
            log.info("直播间 ID [{}] 已存在，重新生成", roomId);
        }
        throw new BusinessException("直播间 ID 生成失败，请重试");
    }
}
