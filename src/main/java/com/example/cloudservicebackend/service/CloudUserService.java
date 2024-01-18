package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.config.SecurityConfig;
import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.repository.CloudRoleRepository;
import com.example.cloudservicebackend.repository.CloudUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Service
@RequiredArgsConstructor
public class CloudUserService implements UserDetailsService {
    private final CloudRoleRepository cloudRoleRepository;
    private final CloudUserRepository cloudUserRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        CloudUser cloudUser = getUserByLogin(login);

        return User.builder()
                .username(cloudUser.getLogin())
                .password(cloudUser.getPassword())
                .authorities(getUserAuthority(cloudUser))
                .build();
    }

    public CloudUser createUser(String login, String password) {
        return cloudUserRepository.save(CloudUser.builder()
                .login(login)
                .password(new BCryptPasswordEncoder().encode(password))
                .roles(Collections.singletonList(cloudRoleRepository.findByName("user")))
                .build());
    }

    public boolean isUserExists(String login) {
        return cloudUserRepository.existsByLogin(login);
    }

    public CloudUser getUserByLogin(String login) {
        return cloudUserRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(format("User {0} not found", login)));
    }

    public CloudUser getUserById(long id) {
        return cloudUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(format("User with id {0} not found", id)));
    }

    private Collection<GrantedAuthority> getUserAuthority(CloudUser user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}