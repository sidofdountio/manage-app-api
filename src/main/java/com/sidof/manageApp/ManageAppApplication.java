package com.sidof.manageApp;

import com.sidof.manageApp.security.model.Permission;
import com.sidof.manageApp.security.model.Role;
import com.sidof.manageApp.security.model.User;
import com.sidof.manageApp.security.repo.PermissionRepository;
import com.sidof.manageApp.security.repo.RoleRepository;
import com.sidof.manageApp.security.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Set;


@EnableTransactionManagement
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class ManageAppApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ManageAppApplication.class, args);
    }
}
