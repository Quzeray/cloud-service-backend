package com.example.cloudservicebackend.repository;

import com.example.cloudservicebackend.entity.CloudUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CloudUserRepository extends JpaRepository<CloudUser, Long> {
    Optional<CloudUser> findByLogin(String username);

    boolean existsByLogin(String username);
}
