package cn.ariven.openaimpbackend.component;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    private static String vatsimDataUrlStatic; // 静态变量接收值

    @Value("${url.whazzup}")
    private String vatsimDataUrl; // 实例变量注入值

    @PostConstruct
    public void init() {
        vatsimDataUrlStatic = vatsimDataUrl; // 赋值给静态变量
    }

    // 提供静态访问方法
    public static String getVatsimDataUrl() {
        return vatsimDataUrlStatic;
    }
}
