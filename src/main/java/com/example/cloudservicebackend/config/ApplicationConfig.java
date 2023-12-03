package com.example.cloudservicebackend.config;

import com.example.cloudservicebackend.entity.CloudFile;
import com.example.cloudservicebackend.entity.CloudRole;
import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.repository.CloudFileRepository;
import com.example.cloudservicebackend.repository.CloudRoleRepository;
import com.example.cloudservicebackend.repository.CloudUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class ApplicationConfig {
    private final CloudRoleRepository cloudRoleRepository;
    private final CloudUserRepository cloudUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Order(1)
    CommandLineRunner initRole() {
        return args -> {
            if (cloudRoleRepository.count() == 0) {
                cloudRoleRepository.save(CloudRole.builder()
                        .name("user")
                        .build());
                cloudRoleRepository.save(CloudRole.builder()
                        .name("admin")
                        .build());
            }
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner initUser() {
        return args -> {
            if (cloudUserRepository.count() == 0) {
                cloudUserRepository.save(CloudUser.builder()
                        .login("user")
                        .password(passwordEncoder.encode("password"))
                        .roles(Collections.singletonList(cloudRoleRepository.findByName("user")))
                        .build());
                cloudUserRepository.save(CloudUser.builder()
                        .id(100L)
                        .login("admin")
                        .password(passwordEncoder.encode("password"))
                        .roles(Collections.singletonList(cloudRoleRepository.findByName("admin")))
                        .build());
            }
        };
    }
}
