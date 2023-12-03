package com.example.cloudservicebackend.utils;

import com.example.cloudservicebackend.entity.CloudFile;
import com.example.cloudservicebackend.entity.CloudRole;
import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.repository.CloudFileRepository;
import com.example.cloudservicebackend.repository.CloudRoleRepository;
import com.example.cloudservicebackend.repository.CloudUserRepository;
import com.example.cloudservicebackend.utils.JwtTokenUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class TestDataUtils {
    @Autowired
    private CloudUserRepository cloudUserRepository;
    @Autowired
    private CloudRoleRepository cloudRoleRepository;
    @Autowired
    private CloudFileRepository cloudFileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    public String createTestBearerToken(String login, String password, long id) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);
        return "Bearer " + jwtTokenUtils.generateToken(authentication, id);
    }

    @Transactional
    public void createCloudUser(String login, String password) {
        cloudUserRepository.saveAndFlush(createCloudUser(login, password, List.of(), List.of()));
    }

    @Transactional
    public void createCloudUserWithRoles(String login, String password, Collection<CloudRole> cloudRoles) {
        cloudRoleRepository.saveAllAndFlush(cloudRoles);
        cloudUserRepository.saveAndFlush(createCloudUser(login, password, cloudRoles, List.of()));
    }

    @Transactional
    public void createCloudUserWithFiles(String login, String password, Collection<CloudFile> cloudFiles) {
        cloudFileRepository.saveAllAndFlush(cloudFiles);
        CloudUser cloudUser = createCloudUser(login, password, List.of(), cloudFiles);
        cloudFiles.forEach(cloudFile -> cloudFile.setUser(cloudUser));
        cloudUserRepository.saveAndFlush(cloudUser);
    }

    @Transactional
    public void createCloudUserWithRolesAndFiles(String login,
                                                 String password,
                                                 Collection<CloudRole> cloudRoles,
                                                 Collection<CloudFile> cloudFiles) {
        cloudRoleRepository.saveAllAndFlush(cloudRoles);
        CloudUser cloudUser = createCloudUser(login, password, cloudRoles, cloudFiles);
        cloudFiles.forEach(cloudFile -> cloudFile.setUser(cloudUser));
        cloudFileRepository.saveAllAndFlush(cloudFiles);
        cloudUserRepository.saveAndFlush(cloudUser);
    }

    public void assignFileToUser(CloudFile cloudFile, String login) {
        CloudUser cloudUser = cloudUserRepository.findByLogin(login).orElseThrow();
        cloudFile.setUser(cloudUser);
        cloudFileRepository.saveAndFlush(cloudFile);
    }

    public void createFileForUser(String login, String fileName) {
        CloudUser cloudUser = cloudUserRepository.findByLogin(login).orElseThrow();
        CloudFile cloudFile = createCloudFile(fileName, cloudUser);
        cloudFileRepository.saveAndFlush(cloudFile);
    }

    private CloudUser createCloudUser(String login,
                                      String password,
                                      Collection<CloudRole> cloudRoles,
                                      Collection<CloudFile> cloudFiles) {
        return CloudUser.builder()
                .login(login)
                .password(passwordEncoder.encode(password))
                .roles(cloudRoles)
                .files(cloudFiles)
                .build();
    }

    private CloudFile createCloudFile(String fileName, CloudUser cloudUser) {
        return CloudFile.builder()
                .fileName(fileName)
                .data(new byte[]{0x01, 0x02, 0x03})
                .user(cloudUser)
                .build();
    }
}
