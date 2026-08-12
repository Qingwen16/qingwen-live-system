package com.wen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置类
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatConfig {

    /**
     * 小程序 AppID
     */
    private String appId;

    /**
     * 小程序 AppSecret
     */
    private String appSecret;

    /**
     * 消息推送配置（可选）
     */
    private String token;

    /**
     * 消息推送加密密钥（可选）
     */
    private String aesKey;

    /**
     * 登录凭证校验接口 URL
     */
    private static final String JS_CODE_2_SESSION_TEMPLATE =
        "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={js_code}&grant_type=authorization_code";

    /**
     * 获取手机号接口 URL
     */
    private static final String GET_PHONE_NUMBER_TEMPLATE =
        "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token={access_token}";

    /**
     * 获取 access_token 接口 URL
     */
    private static final String GET_ACCESS_TOKEN_TEMPLATE =
        "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}";

    /**
     * 获取登录凭证校验接口 URL
     * @param code 微信登录凭证
     * @return 完整的请求 URL
     */
    public String getJsCode2SessionUrl(String code) {
        return JS_CODE_2_SESSION_TEMPLATE
                .replace("{appid}", this.appId)
                .replace("{secret}", this.appSecret)
                .replace("{js_code}", code);
    }

    /**
     * 获取手机号接口 URL
     * @param accessToken 访问令牌
     * @return 完整的请求 URL
     */
    public String getGetPhoneNumberUrl(String accessToken) {
        return GET_PHONE_NUMBER_TEMPLATE.replace("{access_token}", accessToken);
    }

    /**
     * 获取 access_token 接口 URL
     * @return 完整的请求 URL
     */
    public String getGetAccessTokenUrl() {
        return GET_ACCESS_TOKEN_TEMPLATE
                .replace("{appid}", this.appId)
                .replace("{secret}", this.appSecret);
    }

    /**
     * 检查配置是否完整
     */
    public boolean isConfigured() {
        return appId != null && !appId.isEmpty() &&
               appSecret != null && !appSecret.isEmpty();
    }

    /**
     * 验证配置
     */
    public void validate() {
        if (!isConfigured()) {
            throw new IllegalStateException(
                "微信小程序配置不完整，请检查 application.yml 中的 wechat.miniapp 配置"
            );
        }
    }
}
