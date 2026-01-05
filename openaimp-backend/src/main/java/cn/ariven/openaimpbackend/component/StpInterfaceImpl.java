package cn.ariven.openaimpbackend.component;

import cn.ariven.openaimpbackend.entity.Permission;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.entity.UserRole;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Custom Permission Verification Interface Implementation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserRepository userRepository;

    /**
     * Return a list of permission codes for an account
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissions = new ArrayList<>();
        int userId = StpUtil.getLoginIdAsInt();
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 1. Permissions based on Rating (Legacy logic)
            int rating = user.getRating();
            for (int i = 1; i <= rating; i++) {
                permissions.add(String.valueOf(i));
            }

            // 2. Permissions from RBAC Roles
            if (user.getRoles() != null) {
                for (UserRole role : user.getRoles()) {
                    if (role.getPermissions() != null) {
                        for (Permission p : role.getPermissions()) {
                            permissions.add(p.getCode());
                        }
                    }
                }
            }
        }
        
        return permissions;
    }

    /**
     * Return a list of role identifiers for an account
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        int userId = StpUtil.getLoginIdAsInt();
        log.info("Fetching roles for user ID: {}", userId);

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            log.warn("User not found with ID: {}", userId);
            return Collections.emptyList();
        }

        User user = userOptional.get();
        List<String> roles = new ArrayList<>();

        // 1. Roles from RBAC
        if (user.getRoles() != null) {
            roles.addAll(user.getRoles().stream()
                    .map(UserRole::getValue)
                    .collect(Collectors.toList()));
        }

        // 2. Legacy Group Roles (from string)
        String group = user.getGroup();
        if (group != null && !group.trim().isEmpty()) {
            for (String role : group.split(",")) {
                String trimmed = role.trim();
                if (!trimmed.isEmpty() && !roles.contains(trimmed)) {
                    roles.add(trimmed);
                }
            }
        }

        // 3. Dynamic roles based on rating (Legacy logic)
        return getStrings(user, roles);
    }

    private List<String> getStrings(User user, List<String> roles) {
        if (user.getRating() > 0) {
            if (!roles.contains("user-normal")) roles.add("user-normal");
            if (user.getRating() > 1) {
                if (!roles.contains("user-pilot")) roles.add("user-pilot");
                if (!roles.contains("user-control")) roles.add("user-control");
            } else {
                if (!roles.contains("user-pilot")) roles.add("user-pilot");
            }
        } else {
            if (!roles.contains("user-sus")) roles.add("user-sus");
        }
        return roles;
    }
}
