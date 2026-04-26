package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestUpsertControlRecord;
import cn.ariven.openaimpbackend.dto.response.ResponseControlRecord;
import cn.ariven.openaimpbackend.service.ControlRecordService;
import cn.dev33.satoken.stp.StpUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/control-records")
@RequiredArgsConstructor
public class ControlRecordController {
  private final ControlRecordService controlRecordService;

  @GetMapping("/users/{userCid}")
  public Result<List<ResponseControlRecord>> listByUserCid(@PathVariable Integer userCid) {
    checkCanAccessUser(userCid);
    return Result.success("获取管制记录列表成功", controlRecordService.listByUserCid(userCid));
  }

  @GetMapping("/{id}")
  public Result<ResponseControlRecord> getById(@PathVariable Long id) {
    StpUtil.checkLogin();
    ResponseControlRecord record = controlRecordService.getById(id);
    checkCanAccessUser(record.getUserCid());
    return Result.success("获取管制记录成功", record);
  }

  @PostMapping("/users/{userCid}")
  public Result<ResponseControlRecord> create(
      @PathVariable Integer userCid,
      @RequestBody RequestUpsertControlRecord requestUpsertControlRecord) {
    checkCanAccessUser(userCid);
    return Result.success(
        "创建管制记录成功", controlRecordService.create(userCid, requestUpsertControlRecord));
  }

  @PutMapping("/{id}")
  public Result<ResponseControlRecord> update(
      @PathVariable Long id, @RequestBody RequestUpsertControlRecord requestUpsertControlRecord) {
    StpUtil.checkLogin();
    ResponseControlRecord existing = controlRecordService.getById(id);
    checkCanAccessUser(existing.getUserCid());
    return Result.success("更新管制记录成功", controlRecordService.update(id, requestUpsertControlRecord));
  }

  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    StpUtil.checkLogin();
    ResponseControlRecord existing = controlRecordService.getById(id);
    checkCanAccessUser(existing.getUserCid());
    controlRecordService.delete(id);
    return Result.success("删除管制记录成功", null);
  }

  private void checkCanAccessUser(Integer userCid) {
    StpUtil.checkLogin();
    Integer currentUserCid = Integer.valueOf(StpUtil.getLoginIdAsString());
    if (!currentUserCid.equals(userCid) && !StpUtil.hasRole(RbacConstants.ROLE_SUPER_ADMIN)) {
      throw new IllegalArgumentException("无权操作该用户的管制记录");
    }
  }
}
