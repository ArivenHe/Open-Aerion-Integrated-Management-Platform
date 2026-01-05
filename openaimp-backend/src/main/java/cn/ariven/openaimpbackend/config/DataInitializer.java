package cn.ariven.openaimpbackend.config;

import cn.ariven.openaimpbackend.entity.MapConfig;
import cn.ariven.openaimpbackend.entity.PlatformConfig;
import cn.ariven.openaimpbackend.entity.UserRole;
import cn.ariven.openaimpbackend.repository.MapConfigRepository;
import cn.ariven.openaimpbackend.repository.PlatformConfigRepository;
import cn.ariven.openaimpbackend.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(PlatformConfigRepository repository) {
        return args -> {
            // 检查是否已存在数据
            if (repository.count() == 0) {
                PlatformConfig config = new PlatformConfig();
                config.setFirstInstall(1);
                config.setMapModel(0);
                repository.save(config);
                System.out.println("初始化平台配置数据完成");
            }
        };
    }

    @Bean
    public CommandLineRunner initMaoConfigData(MapConfigRepository repository) {
        return args -> {
            // 检查是否已存在数据
            if (repository.count() == 0) {
                MapConfig mapConfig = new MapConfig();
                System.out.println("开始初始化MapBoxConfig");
                mapConfig.setToken("pk.eyJ1IjoiOHY3YzEiLCJhIjoiY21hcGdiM3RtMGdvZzJpcTNqdDA4Nnk4aiJ9.yrfQ7eSinZqy6SBKsr2tgA");
                mapConfig.setStyle("mapbox://styles/mapbox/standard");
                repository.save(mapConfig);
                System.out.println("初始化MapBoxConfig数据完成");
            }
        };
    }

    @Bean
    public CommandLineRunner initUserRole(UserRoleRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                System.out.println("开始初始化UserRoleList");

                List<String> roles = Arrays.asList(
                        "super-admin", "user-rating-edit", "event-posting",
                        "delete-event", "change-event-status"
                );

                for (String role : roles) {
                    UserRole userRole = new UserRole();
                    userRole.setLabel(role);
                    userRole.setValue(role);
                    repository.save(userRole);
                }

                System.out.println("初始化UserRoleList数据完成");
            }
        };
    }
}
