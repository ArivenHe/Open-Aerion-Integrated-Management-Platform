package cn.ariven.openaimpbackend.component;

import cn.ariven.openaimpbackend.pojo.Permission;
import cn.ariven.openaimpbackend.pojo.Role;
import cn.ariven.openaimpbackend.repository.PermissionRepository;
import cn.ariven.openaimpbackend.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppConfig {

    private static String vatsimDataUrlStatic; // 静态变量接收值

    @Value("${url.whazzup}")
    private String vatsimDataUrl; // 实例变量注入值
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @PostConstruct
    public void init() {
        vatsimDataUrlStatic = vatsimDataUrl; // 赋值给静态变量
        initDefaultRoles();
        initDefaultPermissions();
        bindDefaultRolePermissions();
    }

    // 提供静态访问方法
    public static String getVatsimDataUrl() {
        return vatsimDataUrlStatic;
    }

    private void initDefaultRoles() {
        ensureRoleExists("admin", "管理员");
        ensureRoleExists("user", "普通用户");
        ensureRoleExists("banned", "封禁用户");
    }

    private void initDefaultPermissions() {
        ensurePermissionExists("rbac:role:create", "创建角色");
        ensurePermissionExists("rbac:role:update", "更新角色");
        ensurePermissionExists("rbac:role:delete", "删除角色");
        ensurePermissionExists("rbac:role:list", "角色列表");
        ensurePermissionExists("rbac:role:assign-permissions", "分配角色权限");
        ensurePermissionExists("rbac:role:permissions", "角色权限列表");
        ensurePermissionExists("rbac:permission:create", "创建权限");
        ensurePermissionExists("rbac:permission:update", "更新权限");
        ensurePermissionExists("rbac:permission:delete", "删除权限");
        ensurePermissionExists("rbac:permission:list", "权限列表");
        ensurePermissionExists("rbac:user:assign-roles", "分配用户角色");
        ensurePermissionExists("rbac:user:roles", "用户角色列表");
    }

    private void bindDefaultRolePermissions() {
        Role admin = roleRepository.findByName("admin");
        if (admin != null) {
            admin.setPermissions(permissionRepository.findAll());
            roleRepository.save(admin);
        }
        Role user = roleRepository.findByName("user");
        if (user != null && user.getPermissions() == null) {
            user.setPermissions(new ArrayList<>());
            roleRepository.save(user);
        }
        Role banned = roleRepository.findByName("banned");
        if (banned != null && banned.getPermissions() == null) {
            banned.setPermissions(new ArrayList<>());
            roleRepository.save(banned);
        }
    }

    private void ensureRoleExists(String name, String description) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            Role newRole = new Role();
            newRole.setName(name);
            newRole.setDescription(description);
            roleRepository.save(newRole);
        }
    }

    private void ensurePermissionExists(String code, String description) {
        Permission permission = permissionRepository.findByCode(code);
        if (permission == null) {
            Permission newPermission = new Permission();
            newPermission.setCode(code);
            newPermission.setDescription(description);
            permissionRepository.save(newPermission);
        }
    }
}
