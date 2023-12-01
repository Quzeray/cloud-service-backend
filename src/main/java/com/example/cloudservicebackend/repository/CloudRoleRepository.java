package com.example.cloudservicebackend.repository;

import com.example.cloudservicebackend.entity.CloudRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudRoleRepository extends CrudRepository<CloudRole, Integer> {
    CloudRole findByName(String name);
}
