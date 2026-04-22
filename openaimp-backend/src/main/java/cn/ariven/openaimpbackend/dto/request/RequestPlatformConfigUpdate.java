package cn.ariven.openaimpbackend.dto.request;

import cn.ariven.openaimpbackend.pojo.Platform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestPlatformConfigUpdate {
  private Integer id;
  private String platformName;
  private String platformDescription;
  private String platformLogo;
  private String platformUrl;
  private String platformSignedUserCount;
  private String platformCreateTime;

  public Platform toPlatform() {
    return Platform.builder()
        .id(id)
        .platformName(platformName)
        .platformDescription(platformDescription)
        .platformLogo(platformLogo)
        .platformUrl(platformUrl)
        .platformSignedUserCount(platformSignedUserCount)
        .platformCreateTime(platformCreateTime)
        .build();
  }
}
