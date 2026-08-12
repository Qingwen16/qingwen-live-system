package com.wen.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author : rjw
 * @date : 2026-03-16
 * 跨域配置类
 * 用于解决前后端分离架构下的跨域请求问题
 */
@Configuration
public class CorsConfig {

    /**
     * 配置跨域过滤器
     * @return CORS过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        // 创建CORS配置对象
        CorsConfiguration corsConf = new CorsConfiguration();

        // 允许携带认证信息（如Cookie、Authorization头等）
        corsConf.setAllowCredentials(true);

        // 允许所有来源的跨域请求（生产环境建议配置具体的域名）
        corsConf.addAllowedOriginPattern(CorsConfiguration.ALL);

        // 允许所有HTTP请求方法（GET、POST、PUT、DELETE等）
        corsConf.addAllowedMethod(CorsConfiguration.ALL);

        // 允许所有请求头
        corsConf.addAllowedHeader(CorsConfiguration.ALL);

        // 设置预检请求的缓存时间为3600秒（1小时）
        // 在此时间内，浏览器不会再次发送预检请求
        corsConf.setMaxAge(3600L);

        // 创建CORS配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 为所有路径应用CORS配置
        source.registerCorsConfiguration("/**", corsConf);

        // 创建过滤器注册Bean
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(new CorsFilter(source));

        // 设置过滤器名称
        corsBean.setName("corsFilter");

        // 设置过滤器优先级为最高（0表示最先执行）
        corsBean.setOrder(0);

        return corsBean;
    }

}