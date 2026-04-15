package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestFsdIssueToken;
import cn.ariven.openaimpbackend.dto.request.RequestFsdKickUser;
import cn.ariven.openaimpbackend.dto.request.RequestFsdParseToken;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdIssueToken;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdOnlineUsers;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdTokenClaims;
import cn.ariven.openaimpbackend.service.FsdService;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fsd")
@RequiredArgsConstructor
public class FsdController {
  private final FsdService fsdService;

  @GetMapping("/online-users")
  public Result<ResponseFsdOnlineUsers> getOnlineUsers() {
    StpUtil.checkRole(RbacConstants.ROLE_SUPER_ADMIN);
    return Result.success("获取 FSD 在线用户成功", fsdService.getOnlineUsers());
  }

  @PostMapping("/kick")
  public Result<Void> kickUser(@RequestBody RequestFsdKickUser requestFsdKickUser) {
    StpUtil.checkRole(RbacConstants.ROLE_SUPER_ADMIN);
    fsdService.kickUser(requestFsdKickUser.getCallsign());
    return Result.success("FSD 用户已踢出", null);
  }

  @PostMapping("/tokens/issue")
  public Result<ResponseFsdIssueToken> issueToken(
      @RequestBody RequestFsdIssueToken requestFsdIssueToken) {
    StpUtil.checkRole(RbacConstants.ROLE_SUPER_ADMIN);
    return Result.success("FSD token 签发成功", fsdService.issueToken(requestFsdIssueToken));
  }

  @PostMapping("/tokens/service")
  public Result<ResponseFsdIssueToken> issueServiceToken() {
    StpUtil.checkRole(RbacConstants.ROLE_SUPER_ADMIN);
    return Result.success("FSD service token 签发成功", fsdService.issueServiceToken());
  }

  @PostMapping("/tokens/parse")
  public Result<ResponseFsdTokenClaims> parseToken(
      @RequestBody RequestFsdParseToken requestFsdParseToken) {
    StpUtil.checkRole(RbacConstants.ROLE_SUPER_ADMIN);
    return Result.success("FSD token 解析成功", fsdService.parseToken(requestFsdParseToken.getToken()));
  }
}
