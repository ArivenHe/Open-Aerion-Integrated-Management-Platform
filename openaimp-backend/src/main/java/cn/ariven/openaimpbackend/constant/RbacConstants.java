package cn.ariven.openaimpbackend.constant;

import java.util.List;

public final class RbacConstants {
  private RbacConstants() {}

  public static final String ROLE_SUPER_ADMIN = "super-admin";
  public static final String ROLE_USER = "user";

  public static final String PERMISSION_PROFILE_READ = "system:profile:read";
  public static final String PERMISSION_RBAC_CATALOG_READ = "system:rbac:catalog:read";
  public static final String PERMISSION_RBAC_USER_READ = "system:rbac:user:read";
  public static final String PERMISSION_ROLE_CREATE = "system:rbac:role:create";
  public static final String PERMISSION_ROLE_DELETE = "system:rbac:role:delete";
  public static final String PERMISSION_PERMISSION_CREATE = "system:rbac:permission:create";
  public static final String PERMISSION_PERMISSION_DELETE = "system:rbac:permission:delete";
  public static final String PERMISSION_USER_ROLE_ASSIGN = "system:rbac:user-role:assign";
  public static final String PERMISSION_USER_ROLE_REVOKE = "system:rbac:user-role:revoke";
  public static final String PERMISSION_ROLE_PERMISSION_ASSIGN = "system:rbac:role-permission:assign";
  public static final String PERMISSION_ROLE_PERMISSION_REVOKE = "system:rbac:role-permission:revoke";

  public static final List<String> SUPER_ADMIN_PERMISSIONS =
      List.of(
          PERMISSION_PROFILE_READ,
          PERMISSION_RBAC_CATALOG_READ,
          PERMISSION_RBAC_USER_READ,
          PERMISSION_ROLE_CREATE,
          PERMISSION_ROLE_DELETE,
          PERMISSION_PERMISSION_CREATE,
          PERMISSION_PERMISSION_DELETE,
          PERMISSION_USER_ROLE_ASSIGN,
          PERMISSION_USER_ROLE_REVOKE,
          PERMISSION_ROLE_PERMISSION_ASSIGN,
          PERMISSION_ROLE_PERMISSION_REVOKE);
}
