package cn.ariven.openaimpbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.fsd")
public class FsdProperties {
  private String serviceBaseUrl;
  private String webBaseUrl;
  private String jwtSecret;
  private Integer serviceCid;
  private String serviceFirstName;
  private String serviceLastName;
  private Integer serviceNetworkRating;
  private Long serviceTokenValiditySeconds;
  private Long requestTimeoutSeconds;
  private Integer defaultUserNetworkRating;
}
