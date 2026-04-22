package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestPlatformConfigUpdate;
import cn.ariven.openaimpbackend.dto.response.global.ResponseString;
import cn.ariven.openaimpbackend.mapper.PlatformMapper;
import cn.ariven.openaimpbackend.pojo.Platform;
import cn.ariven.openaimpbackend.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformServiceImpl implements PlatformService {
  private final PlatformMapper platformMapper;

  @Override
  public Result<Platform> getPlatformConfig() {
    return platformMapper
        .findFirstByOrderByIdAsc()
        .map(Result::success)
        .orElseGet(() -> Result.fail("平台配置不存在", null));
  }

  @Override
  public Result<ResponseString> updatePlatformConfig(
      RequestPlatformConfigUpdate requestPlatformConfigUpdate) {
    platformMapper.save(requestPlatformConfigUpdate.toPlatform());
    return Result.success(ResponseString.builder().isSuccess(true).message("更新成功").build());
  }
}
