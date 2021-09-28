package com.tfkconsult.amigoscode.repository;

import com.tfkconsult.amigoscode.domain.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
