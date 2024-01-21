package com.example.cloudservicebackend.repository;

import com.example.cloudservicebackend.entity.CloudFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloudFileRepository extends JpaRepository<CloudFile, Long> {
    CloudFile findByFileNameAndUserId(String fileName, long userId);
    List<CloudFile> findAllByUserId(long userId, Pageable pageable);
    List<CloudFile> findAllByUserId(long userId);
    void deleteByFileNameAndUserId(String fileName, long userId);
    boolean existsByFileNameAndUserId(String fileName, long userId);
}