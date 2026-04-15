package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.response.ResponseCurrentAuthorization;
import cn.ariven.openaimpbackend.dto.response.ResponseRbacCatalog;
import cn.ariven.openaimpbackend.pojo.Auth;
import java.util.List;

public interface RbacService {
  void ensureDefaultRolesForNewUser(Auth auth);

  ResponseCurrentAuthorization getCurrentUserAuthorization();

  ResponseCurrentAuthorization getUserAuthorization(Integer userId);

  ResponseCurrentAuthorization assignRolesToUser(Integer userId, List<String> roleCodes);

  ResponseCurrentAuthorization revokeRoleFromUser(Integer userId, String roleCode);

  ResponseRbacCatalog.RoleView assignPermissionsToRole(String roleCode, List<String> permissionCodes);

  ResponseRbacCatalog.RoleView revokePermissionFromRole(String roleCode, String permissionCode);

  ResponseRbacCatalog.RoleView createRole(String code, String name, String description);

  void deleteRole(String roleCode);

  ResponseRbacCatalog.PermissionView createPermission(String code, String name, String description);

  void deletePermission(String permissionCode);

  ResponseRbacCatalog getCatalog();

  List<String> getRoleCodesByUserId(Integer userId);

  List<String> getPermissionCodesByUserId(Integer userId);
}
