package com.example.cloudservicebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.Collection;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cloud_user")
public class CloudUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true, nullable = false)
    private String login;

    @NonNull
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = @JoinColumn(name = "cloud_user_id"),
            inverseJoinColumns = @JoinColumn(name = "cloud_role_id")
    )
    private Collection<CloudRole> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Collection<CloudFile> files;
}