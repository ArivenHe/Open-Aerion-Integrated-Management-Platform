package cn.ariven.openaimpbackend.config;

import cn.ariven.openaimpbackend.constant.FsdConstants;
import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.mapper.AtcMapper;
import cn.ariven.openaimpbackend.mapper.*;
import cn.ariven.openaimpbackend.pojo.Atc;
import cn.ariven.openaimpbackend.pojo.Auth;
import cn.ariven.openaimpbackend.pojo.RbacPermission;
import cn.ariven.openaimpbackend.pojo.RbacRole;
import cn.ariven.openaimpbackend.pojo.RbacRolePermission;
import cn.ariven.openaimpbackend.pojo.RbacUserRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(20)
@RequiredArgsConstructor
public class RbacBootstrapRunner implements ApplicationRunner {
  private final AuthMapper authMapper;
  private final AtcMapper atcMapper;
  private final RbacRoleMapper rbacRoleMapper;
  private final RbacPermissionMapper rbacPermissionMapper;
  private final RbacUserRoleMapper rbacUserRoleMapper;
  private final RbacRolePermissionMapper rbacRolePermissionMapper;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    RbacRole userRole = ensureRole(RbacConstants.ROLE_USER, "普通用户", "平台默认角色", true);
    RbacRole superAdminRole =
        ensureRole(RbacConstants.ROLE_SUPER_ADMIN, "超级管理员", "拥有系统内全部 RBAC 管理能力", true);

    RbacPermission profileRead =
        ensurePermission(RbacConstants.PERMISSION_PROFILE_READ, "读取个人权限信息", "查看当前登录用户的角色和权限", true);
    RbacPermission rbacCatalogRead =
        ensurePermission(
            RbacConstants.PERMISSION_RBAC_CATALOG_READ, "查看 RBAC 目录", "查看系统角色和权限目录", true);
    RbacPermission rbacUserRead =
        ensurePermission(
            RbacConstants.PERMISSION_RBAC_USER_READ, "查看用户授权信息", "查看指定用户的角色和权限", true);
    RbacPermission roleCreate =
        ensurePermission(RbacConstants.PERMISSION_ROLE_CREATE, "创建角色", "创建自定义角色", true);
    RbacPermission roleDelete =
        ensurePermission(RbacConstants.PERMISSION_ROLE_DELETE, "删除角色", "删除自定义角色", true);
    RbacPermission permissionCreate =
        ensurePermission(RbacConstants.PERMISSION_PERMISSION_CREATE, "创建权限", "创建自定义权限", true);
    RbacPermission permissionDelete =
        ensurePermission(RbacConstants.PERMISSION_PERMISSION_DELETE, "删除权限", "删除自定义权限", true);
    RbacPermission userRoleAssign =
        ensurePermission(
            RbacConstants.PERMISSION_USER_ROLE_ASSIGN, "用户分配角色", "给指定用户绑定或调整角色", true);
    RbacPermission userRoleRevoke =
        ensurePermission(
            RbacConstants.PERMISSION_USER_ROLE_REVOKE, "用户移除角色", "从指定用户移除某个角色", true);
    RbacPermission rolePermissionAssign =
        ensurePermission(
            RbacConstants.PERMISSION_ROLE_PERMISSION_ASSIGN,
            "角色分配权限",
            "给指定角色绑定权限",
            true);
    RbacPermission rolePermissionRevoke =
        ensurePermission(
            RbacConstants.PERMISSION_ROLE_PERMISSION_REVOKE,
            "角色移除权限",
            "从指定角色移除某个权限",
            true);

    ensureRolePermission(userRole.getId(), profileRead.getId());
    ensureRolePermission(superAdminRole.getId(), profileRead.getId());
    ensureRolePermission(superAdminRole.getId(), rbacCatalogRead.getId());
    ensureRolePermission(superAdminRole.getId(), rbacUserRead.getId());
    ensureRolePermission(superAdminRole.getId(), roleCreate.getId());
    ensureRolePermission(superAdminRole.getId(), roleDelete.getId());
    ensureRolePermission(superAdminRole.getId(), permissionCreate.getId());
    ensureRolePermission(superAdminRole.getId(), permissionDelete.getId());
    ensureRolePermission(superAdminRole.getId(), userRoleAssign.getId());
    ensureRolePermission(superAdminRole.getId(), userRoleRevoke.getId());
    ensureRolePermission(superAdminRole.getId(), rolePermissionAssign.getId());
    ensureRolePermission(superAdminRole.getId(), rolePermissionRevoke.getId());

    List<Auth> users = authMapper.findAllByOrderByCidAsc();
    for (Auth user : users) {
      ensureUserRole(user.getCid(), userRole.getId());
    }

    if (!rbacUserRoleMapper.existsByRoleId(superAdminRole.getId()) && !users.isEmpty()) {
      ensureUserRole(users.get(0).getCid(), superAdminRole.getId());
    }

    for (Auth user : users) {
      ensureAtcBinding(user.getCid(), superAdminRole.getId());
    }
  }

  private RbacRole ensureRole(String code, String name, String description, boolean builtin) {
    return rbacRoleMapper
        .findByCode(code)
        .map(
            role -> {
              if (!Boolean.TRUE.equals(role.getBuiltin()) && builtin) {
                role.setBuiltin(true);
                return rbacRoleMapper.save(role);
              }
              return role;
            })
        .orElseGet(
            () ->
                rbacRoleMapper.save(
                    RbacRole.builder()
                        .code(code)
                        .name(name)
                        .description(description)
                        .builtin(builtin)
                        .build()));
  }

  private RbacPermission ensurePermission(
      String code, String name, String description, boolean builtin) {
    return rbacPermissionMapper
        .findByCode(code)
        .map(
            permission -> {
              if (!Boolean.TRUE.equals(permission.getBuiltin()) && builtin) {
                permission.setBuiltin(true);
                return rbacPermissionMapper.save(permission);
              }
              return permission;
            })
        .orElseGet(
            () ->
                rbacPermissionMapper.save(
                    RbacPermission.builder()
                        .code(code)
                        .name(name)
                        .description(description)
                        .builtin(builtin)
                        .build()));
  }

  private void ensureRolePermission(Long roleId, Long permissionId) {
    if (!rbacRolePermissionMapper.existsByRoleIdAndPermissionId(roleId, permissionId)) {
      rbacRolePermissionMapper.save(
          RbacRolePermission.builder().roleId(roleId).permissionId(permissionId).build());
    }
  }

  private void ensureUserRole(Integer userId, Long roleId) {
    if (!rbacUserRoleMapper.existsByUserIdAndRoleId(userId, roleId)) {
      rbacUserRoleMapper.save(RbacUserRole.builder().userId(userId).roleId(roleId).build());
    }
  }

  private void ensureAtcBinding(Integer userId, Long superAdminRoleId) {
    if (atcMapper.existsByUserId(userId)) {
      return;
    }

    int networkRating =
        rbacUserRoleMapper.existsByUserIdAndRoleId(userId, superAdminRoleId)
            ? FsdConstants.NETWORK_RATING_ADMINISTRATOR
            : FsdConstants.NETWORK_RATING_OBSERVER;

    atcMapper.save(
        Atc.builder()
            .userId(userId)
            .networkRating(networkRating)
            .facilityType(0)
            .enabled(Boolean.TRUE)
            .build());
  }
}
