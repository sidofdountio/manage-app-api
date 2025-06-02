package com.sidof.manageApp.security.model.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sidof.manageApp.security.model.enums.Permission.*;

public enum Role {
    USER(Collections.emptySet()),
    ADMIN(Set.of(ADMIN_CREATE,ADMIN_READ)),
    MANAGER(Set.of(MANAGER_CREATE)),
    SYSTEM(Set.of(SYSADMIN_CREATE));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    public Set<Permission> getPermissions() {
        return permissions;
    }

//    public List<SimpleGrantedAuthority> getAuthorities(){
//        List<SimpleGrantedAuthority> authorities = permissions.stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.lastName()))
//                .toList();
//        authorities.add(new SimpleGrantedAuthority("ROLE_"+this.lastName()));
//        return authorities;
//    }

//    public List<SimpleGrantedAuthority> getAuthorities() {
//        var authorities = getPermissions()
//                .stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
//                .collect(Collectors.toList());
//        authorities.add(new SimpleGrantedAuthority(this.name()));
//        return authorities;
//    }


}
