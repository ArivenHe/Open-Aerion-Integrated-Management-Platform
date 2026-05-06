package cn.ariven.openaimpbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // 允许所有的映射路径
    registry
        .addMapping("/**")
        // 允许所有的源域名
        .allowedOriginPatterns("*")
        // 允许所有的请求方法
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        // 允许所有的 Header
        .allowedHeaders("*")
        // 是否允许发送 Cookie
        .allowCredentials(true)
        // 预检请求的有效期，单位为秒 (1小时)
        .maxAge(3600);
  }
}
