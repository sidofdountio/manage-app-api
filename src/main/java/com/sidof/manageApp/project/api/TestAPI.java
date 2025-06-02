package com.sidof.manageApp.project.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved BIS
 * Since    : 5/2/25
 * </blockquote></pre>
 */

@RestController
@RequestMapping("/test-role")
public class TestAPI {

    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hello, guest!";
    }

    @GetMapping("/private/hello")
    public String privateHello(Principal principal) {
        return "Hello, " + principal.getName();
    }

    @GetMapping("/user/home")
    public String userHome() {
        return "Welcome USER";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "Welcome ADMIN";
    }

    @GetMapping("/manager/home")
    public String managerHome() {
        return "Welcome MANAGER";
    }

    @GetMapping("/system/home")
    public String systemHome() {
        return "Welcome SYSTEM";
    }
}
