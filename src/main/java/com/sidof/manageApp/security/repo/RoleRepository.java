package com.sidof.manageApp.security.repo;

import com.sidof.manageApp.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1
 * Licence   : All Right Reserved SIDOF
 * Since    : 5/6/25
 * </blockquote></pre>
 */

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);
}
