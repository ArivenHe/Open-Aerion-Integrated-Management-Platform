package cn.ariven.openaimpbackend.component;

import cn.ariven.openaimpbackend.pojo.Permission;
import cn.ariven.openaimpbackend.pojo.Role;
import cn.ariven.openaimpbackend.pojo.User;
import cn.ariven.openaimpbackend.repository.PermissionRepository;
import cn.ariven.openaimpbackend.repository.RoleRepository;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.ariven.openaimpbackend.util.PasswordEncoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppConfig {

    private static String vatsimDataUrlStatic;

    @Value("${url.whazzup}")
    private String vatsimDataUrl;
    @Value("${app.default-admin.enabled:true}")
    private boolean defaultAdminEnabled;
    @Value("${app.default-admin.callsign:admin}")
    private String defaultAdminCallsign;
    @Value("${app.default-admin.email:admin@openaimp.local}")
    private String defaultAdminEmail;
    @Value("${app.default-admin.password:admin123456}")
    private String defaultAdminPassword;
    @Value("${app.default-admin.qq:}")
    private String defaultAdminQq;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        vatsimDataUrlStatic = vatsimDataUrl;
        initDefaultRoles();
        initDefaultPermissions();
        bindDefaultRolePermissions();
        initDefaultAdminUser();
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
        //rbac权限模型crud模块相关权限
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
        ensurePermissionExists("activity:create", "创建活动");
        ensurePermissionExists("activity:update", "更新活动");
        ensurePermissionExists("activity:delete", "删除活动");
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

    private void initDefaultAdminUser() {
        if (!defaultAdminEnabled) {
            return;
        }
        if (userRepository.existsByCallsign(defaultAdminCallsign) || userRepository.existsByEmail(defaultAdminEmail)) {
            return;
        }

        Role adminRole = roleRepository.findByName("admin");
        if (adminRole == null) {
            return;
        }

        User admin = new User();
        admin.setCallsign(defaultAdminCallsign);
        admin.setEmail(defaultAdminEmail);
        admin.setPassword(PasswordEncoder.encode(defaultAdminPassword));
        admin.setQq(defaultAdminQq);
        admin.setRoles(List.of(adminRole));
        userRepository.save(admin);
    }
}
