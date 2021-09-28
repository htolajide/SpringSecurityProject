package com.tfkconsult.amigoscode.repository;

import com.tfkconsult.amigoscode.domain.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
