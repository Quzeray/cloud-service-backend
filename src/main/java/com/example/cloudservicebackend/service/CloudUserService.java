package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.repository.CloudUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Service
@RequiredArgsConstructor
public class CloudUserService implements UserDetailsService {
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