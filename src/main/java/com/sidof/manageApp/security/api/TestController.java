package com.sidof.manageApp.security.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved SIDOF
 * Since    : 5/14/25
 * </blockquote></pre>
 */

@RestController
@RequestMapping("/test")
public class TestController {

    // ✅ Public endpoint (no auth required)
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("✅ Public endpoint — no authentication required");
    }

    // ✅ Role-based endpoint (must have ROLE_ADMIN)
    @GetMapping("/admin")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("🔐 Admin-only endpoint (ROLE_ADMIN required)");
    }

    // ✅ Role-based endpoint (must have ROLE_USER)
    @GetMapping("/user")
    public ResponseEntity<String> userOnly() {
        return ResponseEntity.ok("👤 User-only endpoint (ROLE_USER required)");
    }

    // ✅ Permission-based endpoint
    @PreAuthorize("hasAuthority('USER_READ')")
    @GetMapping("/perm-read")
    public ResponseEntity<String> permissionRead() {
        return ResponseEntity.ok("📖 You have USER_READ permission");
    }

    @PreAuthorize("hasAuthority('USER_DELETE')")
    @DeleteMapping("/perm-delete")
    public ResponseEntity<String> permissionDelete() {
        return ResponseEntity.ok("❌ You have USER_DELETE permission");
    }

    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @PostMapping("/perm-createNewUser-product")
    public ResponseEntity<String> createProduct() {
        return ResponseEntity.ok("🆕 Product created (permission: PRODUCT_CREATE)");
    }
}
