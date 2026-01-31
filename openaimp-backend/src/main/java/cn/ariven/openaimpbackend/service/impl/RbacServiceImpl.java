package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.rbac.*;
import cn.ariven.openaimpbackend.dto.response.rbac.ResponsePermission;
import cn.ariven.openaimpbackend.dto.response.rbac.ResponseRole;
import cn.ariven.openaimpbackend.pojo.Permission;
import cn.ariven.openaimpbackend.pojo.Role;
import cn.ariven.openaimpbackend.pojo.User;
import cn.ariven.openaimpbackend.repository.PermissionRepository;
import cn.ariven.openaimpbackend.repository.RoleRepository;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.ariven.openaimpbackend.service.RbacService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RbacServiceImpl implements RbacService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Result<Void> createRole(RequestRoleCreate request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        roleRepository.save(role);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> updateRole(RequestRoleUpdate request) {
        Role role = roleRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        roleRepository.save(role);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> deleteRole(RequestRoleDelete request) {
        roleRepository.deleteById(request.getId());
        return Result.success();
    }

    @Override
    public Result<List<ResponseRole>> listRoles(RequestRoleList request) {
        List<ResponseRole> roles = roleRepository.findAll().stream()
                .map(this::toResponseRole)
                .collect(Collectors.toList());
        return Result.success(roles);
    }

    @Override
    @Transactional
    public Result<Void> assignPermissionsToRole(RequestRoleAssignPermissions request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        List<Permission> permissions = new ArrayList<>();
        if (request.getPermissionCodes() != null) {
            for (String code : request.getPermissionCodes()) {
                Permission permission = permissionRepository.findByCode(code);
                if (permission == null) {
                    throw new RuntimeException("Permission not found: " + code);
                }
                permissions.add(permission);
            }
        }
        role.setPermissions(permissions);
        roleRepository.save(role);
        return Result.success();
    }

    @Override
    public Result<List<ResponsePermission>> listRolePermissions(RequestRoleId request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        if (role.getPermissions() == null) {
            return Result.success(new ArrayList<>());
        }
        List<ResponsePermission> permissions = role.getPermissions().stream()
                .map(this::toResponsePermission)
                .collect(Collectors.toList());
        return Result.success(permissions);
    }

    @Override
    @Transactional
    public Result<Void> createPermission(RequestPermissionCreate request) {
        Permission permission = new Permission();
        permission.setCode(request.getCode());
        permission.setDescription(request.getDescription());
        permissionRepository.save(permission);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> updatePermission(RequestPermissionUpdate request) {
        Permission permission = permissionRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        permission.setCode(request.getCode());
        permission.setDescription(request.getDescription());
        permissionRepository.save(permission);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> deletePermission(RequestPermissionDelete request) {
        permissionRepository.deleteById(request.getId());
        return Result.success();
    }

    @Override
    public Result<List<ResponsePermission>> listPermissions(RequestPermissionList request) {
        List<ResponsePermission> permissions = permissionRepository.findAll().stream()
                .map(this::toResponsePermission)
                .collect(Collectors.toList());
        return Result.success(permissions);
    }

    @Override
    @Transactional
    public Result<Void> assignRolesToUser(RequestUserAssignRoles request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Role> roles = new ArrayList<>();
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<Role> found = roleRepository.findAllById(request.getRoleIds());
            if (found.size() != request.getRoleIds().size()) {
                throw new RuntimeException("Role not found");
            }
            roles = found;
        }
        user.setRoles(roles);
        userRepository.save(user);
        return Result.success();
    }

    @Override
    public Result<List<ResponseRole>> listUserRoles(RequestUserId request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRoles() == null) {
            return Result.success(new ArrayList<>());
        }
        List<ResponseRole> roles = user.getRoles().stream()
                .map(this::toResponseRole)
                .collect(Collectors.toList());
        return Result.success(roles);
    }

    private ResponseRole toResponseRole(Role role) {
        ResponseRole responseRole = new ResponseRole();
        responseRole.setId(role.getId());
        responseRole.setName(role.getName());
        responseRole.setDescription(role.getDescription());
        if (role.getPermissions() == null) {
            responseRole.setPermissions(new ArrayList<>());
        } else {
            responseRole.setPermissions(
                    role.getPermissions().stream()
                            .map(Permission::getCode)
                            .collect(Collectors.toList())
            );
        }
        return responseRole;
    }

    private ResponsePermission toResponsePermission(Permission permission) {
        ResponsePermission responsePermission = new ResponsePermission();
        responsePermission.setId(permission.getId());
        responsePermission.setCode(permission.getCode());
        responsePermission.setDescription(permission.getDescription());
        return responsePermission;
    }
}
