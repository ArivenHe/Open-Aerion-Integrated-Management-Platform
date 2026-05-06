package cn.ariven.openaimpbackend.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

  /** 1. 注册 Sa-Token 拦截器 */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
        .addPathPatterns("/**")
        .excludePathPatterns("/auth/**")
        .excludePathPatterns("/captcha/**");
  }

  /** 2. 跨域过滤器 (最高优先级) 这种方式比 addCorsMappings 更稳定，能解决预检请求 (OPTIONS) 被拦截的问题 */
  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    // 是否允许发送 Cookie
    config.setAllowCredentials(true);
    // 允许所有的域名 patterns
    config.addAllowedOriginPattern("*");
    // 允许所有的 Header
    config.addAllowedHeader("*");
    // 允许所有的请求方法 (GET, POST...)
    config.addAllowedMethod("*");
    // 预检请求有效期 (1小时)
    config.setMaxAge(3600L);

    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
