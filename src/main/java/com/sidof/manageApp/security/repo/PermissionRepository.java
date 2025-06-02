package com.sidof.manageApp.security.repo;

import com.sidof.manageApp.security.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1
 * Licence   : All Right Reserved BIS
 * Since    : 5/6/25
 * </blockquote></pre>
 */

public interface PermissionRepository extends JpaRepository<Permission,Long> {
    Optional<Permission> findByName(String name);
}
