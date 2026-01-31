package cn.ariven.openaimpbackend.component;

import cn.ariven.openaimpbackend.pojo.Permission;
import cn.ariven.openaimpbackend.pojo.Role;
import cn.ariven.openaimpbackend.pojo.User;
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
        long userId = StpUtil.getLoginIdAsLong();
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> permissionList = new ArrayList<>();
        User user = userOptional.get();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getPermissions() != null) {
                    for (Permission permission : role.getPermissions()) {
                        permissionList.add(permission.getCode());
                    }
                }
            }
        }
        return permissionList;
    }

    /**
     * Return a list of role identifiers for an account
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        long userId = StpUtil.getLoginIdAsLong();
        // log.info("Fetching roles for user ID: {}", userId); // Optional logging

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            // log.warn("User not found with ID: {}", userId);
            return Collections.emptyList();
        }

        List<String> roleList = new ArrayList<>();
        User user = userOptional.get();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                roleList.add(role.getName());
            }
        }
        return roleList;
    }
}
