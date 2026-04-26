package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestUpsertFlightRecord;
import cn.ariven.openaimpbackend.dto.response.ResponseFlightRecord;
import cn.ariven.openaimpbackend.service.FlightRecordService;
import cn.dev33.satoken.stp.StpUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flight-records")
@RequiredArgsConstructor
public class FlightRecordController {
  private final FlightRecordService flightRecordService;

  @GetMapping("/users/{userCid}")
  public Result<List<ResponseFlightRecord>> listByUserCid(@PathVariable Integer userCid) {
    checkCanAccessUser(userCid);
    return Result.success("获取飞行记录列表成功", flightRecordService.listByUserCid(userCid));
  }

  @GetMapping("/{id}")
  public Result<ResponseFlightRecord> getById(@PathVariable Long id) {
    StpUtil.checkLogin();
    ResponseFlightRecord record = flightRecordService.getById(id);
    checkCanAccessUser(record.getUserCid());
    return Result.success("获取飞行记录成功", record);
  }

  @PostMapping("/users/{userCid}")
  public Result<ResponseFlightRecord> create(
      @PathVariable Integer userCid,
      @RequestBody RequestUpsertFlightRecord requestUpsertFlightRecord) {
    checkCanAccessUser(userCid);
    return Result.success(
        "创建飞行记录成功", flightRecordService.create(userCid, requestUpsertFlightRecord));
  }

  @PutMapping("/{id}")
  public Result<ResponseFlightRecord> update(
      @PathVariable Long id, @RequestBody RequestUpsertFlightRecord requestUpsertFlightRecord) {
    StpUtil.checkLogin();
    ResponseFlightRecord existing = flightRecordService.getById(id);
    checkCanAccessUser(existing.getUserCid());
    return Result.success("更新飞行记录成功", flightRecordService.update(id, requestUpsertFlightRecord));
  }

  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    StpUtil.checkLogin();
    ResponseFlightRecord existing = flightRecordService.getById(id);
    checkCanAccessUser(existing.getUserCid());
    flightRecordService.delete(id);
    return Result.success("删除飞行记录成功", null);
  }

  private void checkCanAccessUser(Integer userCid) {
    StpUtil.checkLogin();
    Integer currentUserCid = Integer.valueOf(StpUtil.getLoginIdAsString());
    if (!currentUserCid.equals(userCid) && !StpUtil.hasRole(RbacConstants.ROLE_SUPER_ADMIN)) {
      throw new IllegalArgumentException("无权操作该用户的飞行记录");
    }
  }
}
