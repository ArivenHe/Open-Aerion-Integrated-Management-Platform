package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.rbac.*;
import cn.ariven.openaimpbackend.dto.response.rbac.ResponsePermission;
import cn.ariven.openaimpbackend.dto.response.rbac.ResponseRole;
import cn.ariven.openaimpbackend.service.RbacService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rbac")
@RequiredArgsConstructor
public class RbacController {

    private final RbacService rbacService;

    @PostMapping("/role/create")
    @SaCheckPermission("rbac:role:create")
    public Result<Void> createRole(RequestRoleCreate request) {
        return rbacService.createRole(request);
    }

    @PostMapping("/role/update")
    @SaCheckPermission("rbac:role:update")
    public Result<Void> updateRole(RequestRoleUpdate request) {
        return rbacService.updateRole(request);
    }

    @PostMapping("/role/delete")
    @SaCheckPermission("rbac:role:delete")
    public Result<Void> deleteRole(RequestRoleDelete request) {
        return rbacService.deleteRole(request);
    }

    @PostMapping("/role/list")
    @SaCheckPermission("rbac:role:list")
    public Result<List<ResponseRole>> listRoles(RequestRoleList request) {
        return rbacService.listRoles(request);
    }

    @PostMapping("/role/assign-permissions")
    @SaCheckPermission("rbac:role:assign-permissions")
    public Result<Void> assignPermissionsToRole(RequestRoleAssignPermissions request) {
        return rbacService.assignPermissionsToRole(request);
    }

    @PostMapping("/role/permissions")
    @SaCheckPermission("rbac:role:permissions")
    public Result<List<ResponsePermission>> listRolePermissions(RequestRoleId request) {
        return rbacService.listRolePermissions(request);
    }

    @PostMapping("/permission/create")
    @SaCheckPermission("rbac:permission:create")
    public Result<Void> createPermission(RequestPermissionCreate request) {
        return rbacService.createPermission(request);
    }

    @PostMapping("/permission/update")
    @SaCheckPermission("rbac:permission:update")
    public Result<Void> updatePermission(RequestPermissionUpdate request) {
        return rbacService.updatePermission(request);
    }

    @PostMapping("/permission/delete")
    @SaCheckPermission("rbac:permission:delete")
    public Result<Void> deletePermission(RequestPermissionDelete request) {
        return rbacService.deletePermission(request);
    }

    @PostMapping("/permission/list")
    @SaCheckPermission("rbac:permission:list")
    public Result<List<ResponsePermission>> listPermissions(RequestPermissionList request) {
        return rbacService.listPermissions(request);
    }

    @PostMapping("/user/assign-roles")
    @SaCheckPermission("rbac:user:assign-roles")
    public Result<Void> assignRolesToUser(RequestUserAssignRoles request) {
        return rbacService.assignRolesToUser(request);
    }

    @PostMapping("/user/roles")
    @SaCheckPermission("rbac:user:roles")
    public Result<List<ResponseRole>> listUserRoles(RequestUserId request) {
        return rbacService.listUserRoles(request);
    }
}
