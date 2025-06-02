package com.sidof.manageApp.security.config;

import com.sidof.manageApp.security.model.Permission;
import com.sidof.manageApp.security.model.Role;
import com.sidof.manageApp.security.model.User;
import com.sidof.manageApp.security.repo.PermissionRepository;
import com.sidof.manageApp.security.repo.RoleRepository;
import com.sidof.manageApp.security.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved BIS
 * Since    : 5/7/25
 * </blockquote></pre>
 */

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init() {
        return args -> {
            // 1. Create Permissions
            Permission userRead = createPermission("USER_READ");
            Permission userUpdate = createPermission("USER_UPDATE");
            Permission userDelete = createPermission("USER_DELETE");
            Permission productCreate = createPermission("PRODUCT_CREATE");

            // 2. Create Roles
            Role userRole = createRole("USER", Set.of(userRead));
            Role adminRole = createRole("ADMIN", Set.of(userRead, userUpdate, userDelete, productCreate));

            // 3. Create Admin User
            if (userRepository.findByEmail("admin@manager.com").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin21");
                admin.setFirstName("admin");
                admin.setLastName("admin");
                admin.setEmail("admin@manager.com");
                admin.setEnable(true);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(adminRole);
                userRepository.save(admin);
            }


            if (userRepository.findByEmail("user@manager.com").isEmpty()) {
                User user = new User();
                user.setUsername("user11");
                user.setFirstName("user");
                user.setLastName("user");
                user.setEmail("user@manager.com");
                user.setEnable(true);
                user.setPassword(passwordEncoder.encode("user1122"));
                user.setRole(userRole);
                userRepository.save(user);
            }
        };
    }

    private Permission createPermission(String name) {
        return permissionRepository.findByName(name).orElseGet(() -> permissionRepository.save(new Permission(null, name)));
    }

    private Role createRole(String RoleName, Set<Permission> permissions) {
        return roleRepository.findByName(RoleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(RoleName);
            role.setPermissions(permissions);
            return roleRepository.save(role);
        });
    }
}
