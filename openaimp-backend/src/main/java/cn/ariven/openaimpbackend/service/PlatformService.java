package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestPlatformConfigUpdate;
import cn.ariven.openaimpbackend.dto.response.global.ResponseString;
import cn.ariven.openaimpbackend.pojo.Platform;

public interface PlatformService {
  Result<Platform> getPlatformConfig();

  Result<ResponseString> updatePlatformConfig(
      RequestPlatformConfigUpdate requestPlatformConfigUpdate);
}
