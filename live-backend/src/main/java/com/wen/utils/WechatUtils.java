package com.wen.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wen.common.exception.BusinessException;
import com.wen.config.WechatConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 微信小程序工具类
 * 用于调用微信接口获取用户信息和手机号
 * 
 * @Author : 青灯文案
 * @Date: 2026/4/7
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatUtils {

    private final WechatConfig wechatConfig;

    /**
     * 通过 code 获取 openid 和 session_key
     * @param code 微信登录凭证
     * @return JSONObject 包含 openid, session_key, unionid 等
     */
    public JSONObject getOpenIdAndSessionKey(String code) {
        String url = wechatConfig.getJsCode2SessionUrl(code);
        
        try {
            HttpResponse response = HttpRequest.get(url).execute();
            String body = response.body();
            log.info("微信 jscode2session 响应: {}", body);
            
            JSONObject result = JSONUtil.parseObj(body);
            
            // 检查是否有错误
            if (result.containsKey("errcode") && result.getInt("errcode") != 0) {
                String errmsg = result.getStr("errmsg", "未知错误");
                log.error("微信登录失败: errcode={}, errmsg={}", result.getInt("errcode"), errmsg);
                throw new BusinessException("微信登录失败: " + errmsg);
            }
            
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new BusinessException("微信登录失败，请稍后重试");
        }
    }

    /**
     * 获取 access_token
     * @return access_token
     */
    public String getAccessToken() {
        String url = wechatConfig.getGetAccessTokenUrl();
        
        try {
            HttpResponse response = HttpRequest.get(url).execute();
            String body = response.body();
            log.info("微信 getAccessToken 响应: {}", body);
            
            JSONObject result = JSONUtil.parseObj(body);
            
            // 检查是否有错误
            if (result.containsKey("errcode") && result.getInt("errcode") != 0) {
                String errmsg = result.getStr("errmsg", "未知错误");
                log.error("获取 access_token 失败: errcode={}, errmsg={}", result.getInt("errcode"), errmsg);
                throw new BusinessException("获取 access_token 失败: " + errmsg);
            }
            
            return result.getStr("access_token");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取 access_token 异常", e);
            throw new BusinessException("获取 access_token 失败，请稍后重试");
        }
    }

    /**
     * 通过 phoneCode 获取手机号
     * @param phoneCode 微信小程序 getPhoneNumber 返回的 code
     * @return 手机号
     */
    public String getPhoneNumber(String phoneCode) {
        // 1. 获取 access_token
        String accessToken = getAccessToken();
        
        // 2. 调用获取手机号接口
        String url = wechatConfig.getGetPhoneNumberUrl(accessToken);
        
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.set("code", phoneCode);
            
            HttpResponse response = HttpRequest.post(url)
                    .body(requestBody.toString())
                    .execute();
            
            String body = response.body();
            log.info("微信 getPhoneNumber 响应: {}", body);
            
            JSONObject result = JSONUtil.parseObj(body);
            
            // 检查是否有错误
            if (result.containsKey("errcode") && result.getInt("errcode") != 0) {
                String errmsg = result.getStr("errmsg", "未知错误");
                log.error("获取手机号失败: errcode={}, errmsg={}", result.getInt("errcode"), errmsg);
                throw new BusinessException("获取手机号失败: " + errmsg);
            }
            
            // 解析手机号信息
            JSONObject phoneInfo = result.getJSONObject("phone_info");
            if (phoneInfo == null) {
                throw new BusinessException("获取手机号失败: 返回数据格式错误");
            }
            
            String phoneNumber = phoneInfo.getStr("phoneNumber");
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                throw new BusinessException("获取手机号失败: 手机号为空");
            }
            
            log.info("成功获取微信手机号: {}", phoneNumber);
            return phoneNumber;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取微信手机号异常", e);
            throw new BusinessException("获取手机号失败，请稍后重试");
        }
    }
}
