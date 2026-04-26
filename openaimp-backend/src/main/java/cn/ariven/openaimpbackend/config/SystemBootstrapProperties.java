package cn.ariven.openaimpbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.bootstrap")
public class SystemBootstrapProperties {
  private Integer defaultUserCid = 1;
  private String defaultUserEmail = "admin@openaimp.local";
  private String defaultUserPassword;
  private String platformName = "OpenAIMP";
  private String platformDescription = "Open Aerion Integrated Management Platform";
  private String platformLogo = "";
  private String platformUrl = "";
  private String platformSignedUserCount = "1";
  private String platformCreateTime;
}
