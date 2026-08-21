package com.wen.common.generator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wen.common.exception.BusinessException;
import com.wen.mapper.RoomMapper;
import com.wen.model.entity.RoomEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author : rjw
 * @date : 2026-08-21
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StreamNameGenerator {

    private final RoomMapper roomMapper;

    /**
     * 生成推流名：时间戳(毫秒) + 3 位随机数，落库前校验唯一，冲突则重新生成
     */
    public String generateStreamName() {
        for (int i = 0; i < 3; i++) {
            long name = System.currentTimeMillis() * 1000 + new Random().nextInt(1000);
            String streamName = String.valueOf(name);
            LambdaQueryWrapper<RoomEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RoomEntity::getStreamName, streamName);
            Long count = roomMapper.selectCount(wrapper);
            if (count == null || count == 0) {
                return streamName;
            }
            log.info("推流名 [{}] 已存在，重新生成", streamName);
        }
        throw new BusinessException("推流名生成失败，请重试");
    }

}
