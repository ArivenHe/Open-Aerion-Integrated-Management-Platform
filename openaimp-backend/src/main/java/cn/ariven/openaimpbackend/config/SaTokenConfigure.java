package cn.ariven.openaimpbackend.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

  /** 2. 注册 [Sa-Token 全局过滤器] 解决跨域 */
  @Bean
  public SaServletFilter getSaServletFilter() {
    return new SaServletFilter()
        .addInclude("/**")
        .setBeforeAuth(
            obj -> {
              // ---------- 设置跨域响应头 ----------
              SaHolder.getResponse()
                  .setHeader("Access-Control-Allow-Origin", "*")
                  .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                  .setHeader("Access-Control-Allow-Headers", "*")
                  .setHeader("Access-Control-Max-Age", "3600");

              // 如果是 OPTIONS 预检请求，直接结束请求
              SaRouter.match(SaHttpMethod.OPTIONS)
                  .free(
                      res -> {
                        // 修正：这里不要用 return "ok"，直接用 SaRouter.back() 拦截并返回
                        SaRouter.back("ok");
                      });
            })
        .setAuth(
            obj -> {
              // 这里可以留空
            });
  }
}
