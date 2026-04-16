package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestUpsertAtc;
import cn.ariven.openaimpbackend.dto.response.ResponseAtcBinding;
import cn.ariven.openaimpbackend.service.AtcService;
import cn.dev33.satoken.stp.StpUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atc")
@RequiredArgsConstructor
public class AtcController {
  private final AtcService atcService;

  @GetMapping
  public Result<List<ResponseAtcBinding>> listBindings() {
    StpUtil.checkRole(RbacConstants.ROLE_SUPER_ADMIN);
    return Result.success("获取 ATC 绑定列表成功", atcService.listBindings());
  }

  @GetMapping("/{userId}")
  public Result<ResponseAtcBinding> getBinding(@PathVariable Integer userId) {
    StpUtil.checkRole(RbacConstants.ROLE_SUPER_ADMIN);
    return Result.success("获取 ATC 绑定成功", atcService.getBinding(userId));
  }

  @PostMapping
  public Result<ResponseAtcBinding> upsertBinding(@RequestBody RequestUpsertAtc requestUpsertAtc) {
    StpUtil.checkRole(RbacConstants.ROLE_SUPER_ADMIN);
    return Result.success("ATC 权限绑定成功", atcService.upsertBinding(requestUpsertAtc));
  }

  @DeleteMapping("/{userId}")
  public Result<Void> deleteBinding(@PathVariable Integer userId) {
    StpUtil.checkRole(RbacConstants.ROLE_SUPER_ADMIN);
    atcService.deleteBinding(userId);
    return Result.success("ATC 权限绑定已删除", null);
  }
}
