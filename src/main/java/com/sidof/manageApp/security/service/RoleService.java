package com.sidof.manageApp.security.service;

import com.sidof.manageApp.security.model.Role;
import com.sidof.manageApp.security.repo.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved BIS
 * Since    : 5/6/25
 * </blockquote></pre>
 */

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role getRoleByName(String name){
        return roleRepository.findByName(name).orElseThrow(()-> new EntityNotFoundException("Role with name [" + name + "] does not exist"));
    }

    public Role saveNewRole(String name){
        if(roleRepository.findByName(name).isPresent()){
            throw new IllegalStateException("Role exist with that name :"+name);
        }
        Role  newRole = new Role();
        newRole.setName(name);
        return roleRepository.save(newRole);
    }
}
