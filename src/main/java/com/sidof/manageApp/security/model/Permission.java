package com.sidof.manageApp.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved SIDOF
 * Since    : 5/6/25
 * </blockquote></pre>
 */

@Entity
public class Permission {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name; // e.g., "USER_READ", "USER_DELETE"

    public Permission() {
    }

    public Permission(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public Permission(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Permission(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
