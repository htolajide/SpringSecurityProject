package com.tfkconsult.amigoscode.service;

import java.util.List;

import com.tfkconsult.amigoscode.domain.AppUser;
import com.tfkconsult.amigoscode.domain.Role;

public interface UserService {
    AppUser saveUser(AppUser user);
    Role saveRole(Role role);
    void addRoleUser(String userName, String roleName);
    AppUser getUser(String userName);
    List<AppUser> getUsers();
}
