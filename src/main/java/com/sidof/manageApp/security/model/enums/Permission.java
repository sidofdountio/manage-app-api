package com.sidof.manageApp.security.model.enums;

import lombok.Getter;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1
 * Licence   : All Right Reserved BIS
 * Since    : 4/13/25
 * </blockquote></pre>
 */

@Getter
public enum Permission {
    USER_READ,
    USER_UPDATE,
    USER_DELETE,
    PRODUCT_CREATE,
    ADMIN_READ,
    ADMIN_CREATE,
    ADMIN_DELETE,
    MANAGER_CREATE,
    SYSADMIN_READ,
    SYSADMIN_UPDATE,
    SYSADMIN_DELETE,
    SYSADMIN_CREATE,


}
