package com.tfkconsult.amigoscode.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfkconsult.amigoscode.domain.AppUser;
import com.tfkconsult.amigoscode.domain.Role;
import com.tfkconsult.amigoscode.repository.RoleRepository;
import com.tfkconsult.amigoscode.repository.UserRepository;
import com.tfkconsult.amigoscode.utility.TokenGenerator;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepo; 
    private final RoleRepository roleRepo; 
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    @Override 
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        AppUser user = userRepo.findByUsername(username);
        if(user == null){
            log.error("User not found in database");
            throw new UsernameNotFoundException("User not founf in database");
        } else {
            log.info("User found in database: {}", username);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
    
    @Override
    public AppUser saveUser(AppUser user){
        log.info("Saving new user {}...", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role){
        log.info("Saving new role {}...", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        AppUser user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Fetching user {} ", username);
        return userRepo.findByUsername(username);
    }

    @Override
    public List<AppUser> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                String username = tokenGenerator.getVerifiedUsername(refresh_token);
                AppUser user = getUser(username);
                String access_token = tokenGenerator.getRefreshAccessToken(request, response, user);
                //response.setHeader("access_token", access_token);
                //response.setHeader("refresh_token", refresh_token);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                log.error("Error logging in: {}", exception.getMessage());
                response.setHeader("Error", exception.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                // response.sendError(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }

    }
}
