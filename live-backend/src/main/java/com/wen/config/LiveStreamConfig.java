package com.wen.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云直播（CSS）推拉流配置
 *
 * @author jwruan
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "live.stream")
public class LiveStreamConfig {

    /**
     * 推流域名（主播推流入口），需 ICP 备案并 CNAME 解析到腾讯云
     */
    private String pushDomain;

    /**
     * 播放域名（观众拉流入口），同样需备案
     */
    private String playDomain;

    /**
     * 应用名，腾讯云默认 live
     */
    private String appName = "live";

    /**
     * 推流鉴权 Key（控制台「推流配置」生成的主 Key）
     */
    private String pushKey;

    /**
     * 播放鉴权 Key（可选，仅开启播放鉴权时使用）
     */
    private String playKey;

    /**
     * 推流鉴权有效期（秒），默认 24 小时
     */
    private long pushAuthExpireSeconds = 86400L;

    /**
     * 是否开启播放鉴权
     */
    private boolean playAuthEnabled = false;

    /**
     * 播放鉴权有效期（秒），默认 24 小时
     */
    private long playAuthExpireSeconds = 86400L;

    /**
     * 生成推流地址（RTMP）。推流默认开启防盗链；key 未配置时先返回不带鉴权的地址，便于资源申请前联调
     */
    public String buildPushUrl(String streamName) {
        String base = "rtmp://" + pushDomain + "/" + appName + "/" + streamName;
        if (StrUtil.isBlank(pushKey)) {
            return base;
        }
        long txTime = nowSeconds() + pushAuthExpireSeconds;
        String txTimeHex = Long.toHexString(txTime).toUpperCase();
        String txSecret = SecureUtil.md5(pushKey + streamName + txTimeHex);
        return base + "?txSecret=" + txSecret + "&txTime=" + txTimeHex;
    }

    /**
     * 生成拉流地址（播放地址），默认 http-flv 协议。
     * 选 flv 是因为 Web/H5 端低延迟主流协议（配合 flv.js 播放），移动端原生播放可改 HLS
     */
    public String buildPlayUrl(String streamName) {
        String base = "http://" + playDomain + "/" + appName + "/" + streamName + ".flv";
        if (!playAuthEnabled || StrUtil.isBlank(playKey)) {
            return base;
        }
        long txTime = nowSeconds() + playAuthExpireSeconds;
        String txTimeHex = Long.toHexString(txTime).toUpperCase();
        String txSecret = SecureUtil.md5(playKey + streamName + txTimeHex);
        return base + "?txSecret=" + txSecret + "&txTime=" + txTimeHex;
    }

    private long nowSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
