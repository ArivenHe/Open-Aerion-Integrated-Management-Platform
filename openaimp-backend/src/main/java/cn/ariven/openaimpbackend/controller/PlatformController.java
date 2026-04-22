package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.response.global.ResponseString;
import cn.ariven.openaimpbackend.pojo.Platform;
import cn.ariven.openaimpbackend.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform")
@RequiredArgsConstructor
public class PlatformController {
  private final PlatformService platformService;

  @GetMapping("/config")
  public Result<Platform> getConfig() {
    return platformService.getPlatformConfig();
  }

  @PostMapping("/config/update")
  public Result<ResponseString> updateConfig(Platform platform) {
    return null;
  }
}
