package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.dto.response.ResponseCurrentAuthorization;
import cn.ariven.openaimpbackend.dto.response.ResponseRbacCatalog;
import cn.ariven.openaimpbackend.mapper.*;
import cn.ariven.openaimpbackend.pojo.*;
import cn.ariven.openaimpbackend.service.RbacService;
import cn.dev33.satoken.stp.StpUtil;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IRbacServiceImpl implements RbacService {
  private final AuthMapper authMapper;
  private final RbacRoleMapper rbacRoleMapper;
  private final RbacPermissionMapper rbacPermissionMapper;
  private final RbacUserRoleMapper rbacUserRoleMapper;
  private final RbacRolePermissionMapper rbacRolePermissionMapper;

  @Override
  @Transactional
  public void ensureDefaultRolesForNewUser(Auth auth) {
    RbacRole userRole = getRoleOrThrow(RbacConstants.ROLE_USER);
    ensureUserRole(auth.getCid(), userRole.getId());

    if (!rbacUserRoleMapper.existsByRoleId(getRoleOrThrow(RbacConstants.ROLE_SUPER_ADMIN).getId())
        && authMapper.count() == 1) {
      ensureUserRole(auth.getCid(), getRoleOrThrow(RbacConstants.ROLE_SUPER_ADMIN).getId());
    }
  }

  @Override
  public ResponseCurrentAuthorization getCurrentUserAuthorization() {
    return getUserAuthorization(currentUserId());
  }

  @Override
  public ResponseCurrentAuthorization getUserAuthorization(Integer userId) {
    Auth auth =
        authMapper
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
    return buildAuthorization(auth);
  }

  @Override
  @Transactional
  public ResponseCurrentAuthorization assignRolesToUser(Integer userId, List<String> roleCodes) {
    Auth auth =
        authMapper
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
    List<String> normalizedCodes = normalizeCodes(roleCodes, "角色编码");
    List<RbacRole> roles = rbacRoleMapper.findByCodeIn(normalizedCodes);
    validateAllFound(normalizedCodes, roles.stream().map(RbacRole::getCode).toList(), "角色");

    List<Long> targetRoleIds = roles.stream().map(RbacRole::getId).distinct().toList();
    List<RbacUserRole> existingRelations = rbacUserRoleMapper.findAllByUserId(userId);
    List<RbacUserRole> relationsToRemove =
        existingRelations.stream()
            .filter(relation -> !targetRoleIds.contains(relation.getRoleId()))
            .toList();
    if (!relationsToRemove.isEmpty()) {
      rbacUserRoleMapper.deleteAll(relationsToRemove);
    }

    for (RbacRole role : roles) {
      ensureUserRole(userId, role.getId());
    }
    return buildAuthorization(auth);
  }

  @Override
  @Transactional
  public ResponseCurrentAuthorization revokeRoleFromUser(Integer userId, String roleCode) {
    Auth auth =
        authMapper
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
    RbacRole role = getRoleOrThrow(normalizeCode(roleCode, "角色编码"));
    if (!rbacUserRoleMapper.existsByUserIdAndRoleId(userId, role.getId())) {
      throw new IllegalArgumentException("该用户未绑定角色: " + roleCode);
    }
    rbacUserRoleMapper.deleteAllByUserIdAndRoleId(userId, role.getId());
    return buildAuthorization(auth);
  }

  @Override
  @Transactional
  public ResponseRbacCatalog.RoleView assignPermissionsToRole(
      String roleCode, List<String> permissionCodes) {
    String normalizedRoleCode = normalizeCode(roleCode, "角色编码");
    List<String> normalizedCodes = normalizeCodes(permissionCodes, "权限编码");
    RbacRole role = getRoleOrThrow(normalizedRoleCode);
    List<RbacPermission> permissions = rbacPermissionMapper.findByCodeIn(normalizedCodes);
    validateAllFound(normalizedCodes, permissions.stream().map(RbacPermission::getCode).toList(), "权限");

    List<Long> targetPermissionIds = permissions.stream().map(RbacPermission::getId).distinct().toList();
    List<RbacRolePermission> existingRelations = rbacRolePermissionMapper.findAllByRoleId(role.getId());
    List<RbacRolePermission> relationsToRemove =
        existingRelations.stream()
            .filter(relation -> !targetPermissionIds.contains(relation.getPermissionId()))
            .toList();
    if (!relationsToRemove.isEmpty()) {
      rbacRolePermissionMapper.deleteAll(relationsToRemove);
    }

    for (RbacPermission permission : permissions) {
      ensureRolePermission(role.getId(), permission.getId());
    }
    return buildRoleView(role, getRolePermissionCodesByRoleId(role.getId()));
  }

  @Override
  @Transactional
  public ResponseRbacCatalog.RoleView revokePermissionFromRole(String roleCode, String permissionCode) {
    RbacRole role = getRoleOrThrow(normalizeCode(roleCode, "角色编码"));
    RbacPermission permission = getPermissionOrThrow(normalizeCode(permissionCode, "权限编码"));
    if (!rbacRolePermissionMapper.existsByRoleIdAndPermissionId(role.getId(), permission.getId())) {
      throw new IllegalArgumentException("该角色未绑定权限: " + permissionCode);
    }
    rbacRolePermissionMapper.deleteAllByRoleIdAndPermissionId(role.getId(), permission.getId());
    return buildRoleView(role, getRolePermissionCodesByRoleId(role.getId()));
  }

  @Override
  @Transactional
  public ResponseRbacCatalog.RoleView createRole(String code, String name, String description) {
    String normalizedCode = normalizeCode(code, "角色编码");
    String normalizedName = normalizeCode(name, "角色名称");
    if (rbacRoleMapper.existsByCode(normalizedCode)) {
      throw new IllegalArgumentException("角色编码已存在: " + normalizedCode);
    }
    RbacRole role =
        rbacRoleMapper.save(
            RbacRole.builder()
                .code(normalizedCode)
                .name(normalizedName)
                .description(normalizeNullableText(description))
                .builtin(false)
                .build());
    return buildRoleView(role, List.of());
  }

  @Override
  @Transactional
  public void deleteRole(String roleCode) {
    RbacRole role = getRoleOrThrow(normalizeCode(roleCode, "角色编码"));
    ensureCustomRole(role);
    if (rbacUserRoleMapper.existsByRoleId(role.getId())) {
      throw new IllegalArgumentException("角色仍有用户绑定，无法删除: " + roleCode);
    }
    rbacRolePermissionMapper.deleteAllByRoleId(role.getId());
    rbacRoleMapper.delete(role);
  }

  @Override
  @Transactional
  public ResponseRbacCatalog.PermissionView createPermission(
      String code, String name, String description) {
    String normalizedCode = normalizeCode(code, "权限编码");
    String normalizedName = normalizeCode(name, "权限名称");
    if (rbacPermissionMapper.existsByCode(normalizedCode)) {
      throw new IllegalArgumentException("权限编码已存在: " + normalizedCode);
    }
    RbacPermission permission =
        rbacPermissionMapper.save(
            RbacPermission.builder()
                .code(normalizedCode)
                .name(normalizedName)
                .description(normalizeNullableText(description))
                .builtin(false)
                .build());
    return buildPermissionView(permission);
  }

  @Override
  @Transactional
  public void deletePermission(String permissionCode) {
    RbacPermission permission = getPermissionOrThrow(normalizeCode(permissionCode, "权限编码"));
    ensureCustomPermission(permission);
    if (rbacRolePermissionMapper.existsByPermissionId(permission.getId())) {
      throw new IllegalArgumentException("权限仍被角色引用，无法删除: " + permissionCode);
    }
    rbacPermissionMapper.delete(permission);
  }

  @Override
  public ResponseRbacCatalog getCatalog() {
    Map<Long, RbacPermission> permissionMap = getPermissionMap();
    Map<Long, List<String>> permissionsByRoleId = getRolePermissionCodesMap(permissionMap);

    List<ResponseRbacCatalog.RoleView> roleViews =
        rbacRoleMapper.findAllByOrderByIdAsc().stream()
            .map(role -> buildRoleView(role, permissionsByRoleId.getOrDefault(role.getId(), List.of())))
            .toList();

    List<ResponseRbacCatalog.PermissionView> permissionViews =
        rbacPermissionMapper.findAllByOrderByIdAsc().stream()
            .map(
                permission ->
                    ResponseRbacCatalog.PermissionView.builder()
                        .code(permission.getCode())
                        .name(permission.getName())
                        .description(permission.getDescription())
                        .builtin(permission.getBuiltin())
                        .build())
            .toList();

    return ResponseRbacCatalog.builder().roles(roleViews).permissions(permissionViews).build();
  }

  @Override
  public List<String> getRoleCodesByUserId(Integer userId) {
    List<RbacUserRole> userRoles = rbacUserRoleMapper.findAllByUserId(userId);
    if (userRoles.isEmpty()) {
      return List.of();
    }

    Map<Long, RbacRole> roleMap =
        rbacRoleMapper.findAllById(userRoles.stream().map(RbacUserRole::getRoleId).distinct().toList()).stream()
            .collect(Collectors.toMap(RbacRole::getId, Function.identity()));

    return userRoles.stream()
        .map(RbacUserRole::getRoleId)
        .map(roleMap::get)
        .filter(Objects::nonNull)
        .map(RbacRole::getCode)
        .distinct()
        .sorted()
        .toList();
  }

  @Override
  public List<String> getPermissionCodesByUserId(Integer userId) {
    List<RbacUserRole> userRoles = rbacUserRoleMapper.findAllByUserId(userId);
    if (userRoles.isEmpty()) {
      return List.of();
    }

    List<Long> roleIds = userRoles.stream().map(RbacUserRole::getRoleId).distinct().toList();
    List<RbacRolePermission> rolePermissions = rbacRolePermissionMapper.findAllByRoleIdIn(roleIds);
    if (rolePermissions.isEmpty()) {
      return List.of();
    }

    Map<Long, RbacPermission> permissionMap =
        rbacPermissionMapper.findAllById(
                rolePermissions.stream().map(RbacRolePermission::getPermissionId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(RbacPermission::getId, Function.identity()));

    return rolePermissions.stream()
        .map(RbacRolePermission::getPermissionId)
        .map(permissionMap::get)
        .filter(Objects::nonNull)
        .map(RbacPermission::getCode)
        .distinct()
        .sorted()
        .toList();
  }

  private ResponseCurrentAuthorization buildAuthorization(Auth auth) {
    return ResponseCurrentAuthorization.builder()
        .cid(auth.getCid())
        .email(auth.getEmail())
        .roles(getRoleCodesByUserId(auth.getCid()))
        .permissions(getPermissionCodesByUserId(auth.getCid()))
        .build();
  }

  private Map<Long, RbacPermission> getPermissionMap() {
    return rbacPermissionMapper.findAllByOrderByIdAsc().stream()
        .collect(Collectors.toMap(RbacPermission::getId, Function.identity()));
  }

  private Map<Long, List<String>> getRolePermissionCodesMap(Map<Long, RbacPermission> permissionMap) {
    return rbacRolePermissionMapper.findAll().stream()
        .collect(
            Collectors.groupingBy(
                RbacRolePermission::getRoleId,
                Collectors.mapping(
                    relation -> {
                      RbacPermission permission = permissionMap.get(relation.getPermissionId());
                      return permission == null ? null : permission.getCode();
                    },
                    Collectors.collectingAndThen(
                        Collectors.toCollection(TreeSet::new),
                        codes -> codes.stream().filter(Objects::nonNull).toList()))));
  }

  private List<String> getRolePermissionCodesByRoleId(Long roleId) {
    Map<Long, RbacPermission> permissionMap = getPermissionMap();
    return rbacRolePermissionMapper.findAllByRoleId(roleId).stream()
        .map(relation -> permissionMap.get(relation.getPermissionId()))
        .filter(Objects::nonNull)
        .map(RbacPermission::getCode)
        .distinct()
        .sorted()
        .toList();
  }

  private ResponseRbacCatalog.RoleView buildRoleView(
      RbacRole role, List<String> permissionCodes) {
    List<String> codes = permissionCodes;
    if (codes == null || codes.isEmpty()) {
      codes = getRolePermissionCodesByRoleId(role.getId());
    }
    return ResponseRbacCatalog.RoleView.builder()
        .code(role.getCode())
        .name(role.getName())
        .description(role.getDescription())
        .builtin(role.getBuiltin())
        .permissions(codes)
        .build();
  }

  private ResponseRbacCatalog.PermissionView buildPermissionView(RbacPermission permission) {
    return ResponseRbacCatalog.PermissionView.builder()
        .code(permission.getCode())
        .name(permission.getName())
        .description(permission.getDescription())
        .builtin(permission.getBuiltin())
        .build();
  }

  private void ensureUserRole(Integer userId, Long roleId) {
    if (!rbacUserRoleMapper.existsByUserIdAndRoleId(userId, roleId)) {
      rbacUserRoleMapper.save(RbacUserRole.builder().userId(userId).roleId(roleId).build());
    }
  }

  private void ensureRolePermission(Long roleId, Long permissionId) {
    if (!rbacRolePermissionMapper.existsByRoleIdAndPermissionId(roleId, permissionId)) {
      rbacRolePermissionMapper.save(
          RbacRolePermission.builder().roleId(roleId).permissionId(permissionId).build());
    }
  }

  private RbacRole getRoleOrThrow(String roleCode) {
    return rbacRoleMapper
        .findByCode(roleCode)
        .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + roleCode));
  }

  private RbacPermission getPermissionOrThrow(String permissionCode) {
    return rbacPermissionMapper
        .findByCode(permissionCode)
        .orElseThrow(() -> new IllegalArgumentException("权限不存在: " + permissionCode));
  }

  private void ensureCustomRole(RbacRole role) {
    if (Boolean.TRUE.equals(role.getBuiltin())) {
      throw new IllegalArgumentException("系统内置角色不允许删除: " + role.getCode());
    }
  }

  private void ensureCustomPermission(RbacPermission permission) {
    if (Boolean.TRUE.equals(permission.getBuiltin())) {
      throw new IllegalArgumentException("系统内置权限不允许删除: " + permission.getCode());
    }
  }

  private Integer currentUserId() {
    return Integer.parseInt(String.valueOf(StpUtil.getLoginId()));
  }

  private List<String> normalizeCodes(List<String> codes, String label) {
    if (codes == null || codes.isEmpty()) {
      throw new IllegalArgumentException(label + "不能为空");
    }

    List<String> normalized =
        codes.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(code -> !code.isEmpty())
            .distinct()
            .toList();

    if (normalized.isEmpty()) {
      throw new IllegalArgumentException(label + "不能为空");
    }
    return normalized;
  }

  private String normalizeCode(String code, String label) {
    if (code == null || code.trim().isEmpty()) {
      throw new IllegalArgumentException(label + "不能为空");
    }
    return code.trim();
  }

  private String normalizeNullableText(String text) {
    if (text == null) {
      return null;
    }
    String normalized = text.trim();
    return normalized.isEmpty() ? null : normalized;
  }

  private void validateAllFound(List<String> expectedCodes, List<String> foundCodes, String targetName) {
    List<String> missingCodes =
        expectedCodes.stream().filter(code -> !foundCodes.contains(code)).distinct().toList();
    if (!missingCodes.isEmpty()) {
      throw new IllegalArgumentException(targetName + "不存在: " + String.join(", ", missingCodes));
    }
  }
}
