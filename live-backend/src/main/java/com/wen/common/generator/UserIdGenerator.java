package com.wen.common.generator;

import java.util.Random;

/**
 * 用户ID生成工具类
 * @author : rjw
 * @date : 2026-03-16
 */
public class UserIdGenerator {

    private static final Random RANDOM = new Random();

    /**
     * 生成用户ID
     * 使用时间戳(13位) + 随机数(1位)组合生成
     * @return 用户ID
     */
    public static long generator() {
        // 获取当前时间戳(毫秒)
        long timestamp = System.currentTimeMillis();
        // 生成1位随机数(0-9)
        int randomNum = RANDOM.nextInt(10);
        // 组合生成用户ID
        return timestamp * 10 + randomNum;
    }

}