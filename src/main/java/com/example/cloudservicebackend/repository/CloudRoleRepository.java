package com.example.cloudservicebackend.repository;

import com.example.cloudservicebackend.entity.CloudRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudRoleRepository extends JpaRepository<CloudRole, Integer> {
    CloudRole findByName(String name);
}
