package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.rbac.*;
import cn.ariven.openaimpbackend.dto.response.rbac.ResponsePermission;
import cn.ariven.openaimpbackend.dto.response.rbac.ResponseRole;
import java.util.List;

public interface RbacService {
    Result<Void> createRole(RequestRoleCreate request);
    Result<Void> updateRole(RequestRoleUpdate request);
    Result<Void> deleteRole(RequestRoleDelete request);
    Result<List<ResponseRole>> listRoles(RequestRoleList request);
    Result<Void> assignPermissionsToRole(RequestRoleAssignPermissions request);
    Result<List<ResponsePermission>> listRolePermissions(RequestRoleId request);

    Result<Void> createPermission(RequestPermissionCreate request);
    Result<Void> updatePermission(RequestPermissionUpdate request);
    Result<Void> deletePermission(RequestPermissionDelete request);
    Result<List<ResponsePermission>> listPermissions(RequestPermissionList request);

    Result<Void> assignRolesToUser(RequestUserAssignRoles request);
    Result<List<ResponseRole>> listUserRoles(RequestUserId request);
}
