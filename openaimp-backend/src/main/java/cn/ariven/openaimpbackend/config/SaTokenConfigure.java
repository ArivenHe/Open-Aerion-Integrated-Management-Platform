package cn.ariven.openaimpbackend.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 正确配置：拦截所有路径，但排除 /Auth 开头的请求
            SaRouter
                    .match("/**")                   // 拦截所有路径
                    .notMatch("/auth/**")           // 排除 /auth 下的所有路径
                    .notMatch("/captcha/**")
                    .notMatch("/public/**")
                    .notMatch("/image")
                    .notMatch("/Map/**")
                    .check(r -> StpUtil.checkLogin()); // 登录验证
        })).addPathPatterns("/**");             // 注册拦截器作用于所有路径
    }
}
