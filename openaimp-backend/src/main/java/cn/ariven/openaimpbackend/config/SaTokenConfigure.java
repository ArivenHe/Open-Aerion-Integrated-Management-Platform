package cn.ariven.openaimpbackend.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

  /** 注册 Sa-Token 拦截器 */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
        .addPathPatterns("/**")
        .excludePathPatterns("/auth/**")
        .excludePathPatterns("/captcha/**")
        // 关键：排除掉 OPTIONS 请求，否则预检请求会被拦截器拦下导致跨域失败
        .excludePathPatterns("/**/options");
  }

  /** 跨域配置：允许所有 */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        // 允许所有域名
        .allowedOriginPatterns("*")
        // 允许所有方法
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        // 允许所有 Header
        .allowedHeaders("*")
        // 允许发送 Cookie / Auth Token
        .allowCredentials(true)
        // 预检请求有效期
        .maxAge(3600);
  }
}
