package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestAssignPermissions;
import cn.ariven.openaimpbackend.dto.request.RequestAssignRoles;
import cn.ariven.openaimpbackend.dto.request.RequestCreatePermission;
import cn.ariven.openaimpbackend.dto.request.RequestCreateRole;
import cn.ariven.openaimpbackend.dto.response.ResponseCurrentAuthorization;
import cn.ariven.openaimpbackend.dto.response.ResponseRbacCatalog;
import cn.ariven.openaimpbackend.service.RbacService;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rbac")
@RequiredArgsConstructor
public class RbacController {
  private final RbacService rbacService;

  @GetMapping("/me")
  public Result<ResponseCurrentAuthorization> currentAuthorization() {
    StpUtil.checkPermission(RbacConstants.PERMISSION_PROFILE_READ);
    return Result.success("获取当前 RBAC 信息成功", rbacService.getCurrentUserAuthorization());
  }

  @GetMapping("/users/{cid}")
  public Result<ResponseCurrentAuthorization> userAuthorization(@PathVariable Integer cid) {
    StpUtil.checkPermission(RbacConstants.PERMISSION_RBAC_USER_READ);
    return Result.success("获取用户权限信息成功", rbacService.getUserAuthorization(cid));
  }

  @GetMapping("/catalog")
  public Result<ResponseRbacCatalog> catalog() {
    StpUtil.checkPermission(RbacConstants.PERMISSION_RBAC_CATALOG_READ);
    return Result.success("获取 RBAC 目录成功", rbacService.getCatalog());
  }

  @PostMapping("/roles")
  public Result<ResponseRbacCatalog.RoleView> createRole(
      @RequestBody RequestCreateRole requestCreateRole) {
    StpUtil.checkPermission(RbacConstants.PERMISSION_ROLE_CREATE);
    return Result.success(
        "角色创建成功",
        rbacService.createRole(
            requestCreateRole.getCode(),
            requestCreateRole.getName(),
            requestCreateRole.getDescription()));
  }

  @DeleteMapping("/roles/{roleCode}")
  public Result<Void> deleteRole(@PathVariable String roleCode) {
    StpUtil.checkPermission(RbacConstants.PERMISSION_ROLE_DELETE);
    rbacService.deleteRole(roleCode);
    return Result.success("角色删除成功", null);
  }

  @PostMapping("/permissions")
  public Result<ResponseRbacCatalog.PermissionView> createPermission(
      @RequestBody RequestCreatePermission requestCreatePermission) {
    StpUtil.checkPermission(RbacConstants.PERMISSION_PERMISSION_CREATE);
    return Result.success(
        "权限创建成功",
        rbacService.createPermission(
            requestCreatePermission.getCode(),
            requestCreatePermission.getName(),
            requestCreatePermission.getDescription()));
  }

  @DeleteMapping("/permissions/{permissionCode}")
  public Result<Void> deletePermission(@PathVariable String permissionCode) {
    StpUtil.checkPermission(RbacConstants.PERMISSION_PERMISSION_DELETE);
    rbacService.deletePermission(permissionCode);
    return Result.success("权限删除成功", null);
  }

  @PostMapping("/users/{cid}/roles")
  public Result<ResponseCurrentAuthorization> assignRoles(
      @PathVariable Integer cid, @RequestBody RequestAssignRoles requestAssignRoles) {
    StpUtil.checkPermission(RbacConstants.PERMISSION_USER_ROLE_ASSIGN);
    return Result.success(
        "用户角色分配成功", rbacService.assignRolesToUser(cid, requestAssignRoles.getRoleCodes()));
  }

  @DeleteMapping("/users/{cid}/roles/{roleCode}")
  public Result<ResponseCurrentAuthorization> revokeRole(
      @PathVariable Integer cid, @PathVariable String roleCode) {
    StpUtil.checkPermission(RbacConstants.PERMISSION_USER_ROLE_REVOKE);
    return Result.success("用户角色移除成功", rbacService.revokeRoleFromUser(cid, roleCode));
  }

  @PostMapping("/roles/{roleCode}/permissions")
  public Result<ResponseRbacCatalog.RoleView> assignPermissions(
      @PathVariable String roleCode,
      @RequestBody RequestAssignPermissions requestAssignPermissions) {
    StpUtil.checkPermission(RbacConstants.PERMISSION_ROLE_PERMISSION_ASSIGN);
    return Result.success(
        "角色权限分配成功",
        rbacService.assignPermissionsToRole(roleCode, requestAssignPermissions.getPermissionCodes()));
  }

  @DeleteMapping("/roles/{roleCode}/permissions/{permissionCode}")
  public Result<ResponseRbacCatalog.RoleView> revokePermission(
      @PathVariable String roleCode, @PathVariable String permissionCode) {
    StpUtil.checkPermission(RbacConstants.PERMISSION_ROLE_PERMISSION_REVOKE);
    return Result.success(
        "角色权限移除成功",
        rbacService.revokePermissionFromRole(roleCode, permissionCode));
  }
}
