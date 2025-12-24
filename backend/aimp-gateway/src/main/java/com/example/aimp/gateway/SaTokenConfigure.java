package com.example.aimp.gateway;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaTokenConfigure {
    
    // Register Sa-Token Global Filter
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
            // Intercept path
            .addInclude("/**")
            // Exclude path (Login, Register, etc.)
            .addExclude("/favicon.ico", "/api/login", "/api/user/add") 
            // Auth Logic
            .setAuth(obj -> {
                // Check login
                SaRouter.match("/**", "/api/login", r -> StpUtil.checkLogin());
                
                // More permission checks can be added here
                // SaRouter.match("/api/admin/**", r -> StpUtil.checkRole("admin"));
            })
            // Exception Handling
            .setError(e -> {
                return SaResult.error(e.getMessage());
            });
    }
}
