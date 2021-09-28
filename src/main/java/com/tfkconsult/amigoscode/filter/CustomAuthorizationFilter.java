package com.tfkconsult.amigoscode.filter;

import static java.util.Arrays.stream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfkconsult.amigoscode.utility.TokenGenerator;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final TokenGenerator tokenGenerator = new TokenGenerator();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    String username = tokenGenerator.getVerifiedUsername(token);
                    log.info("Username is {}", username);
                    String[] roles = tokenGenerator.getVerifiedRoles(token);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    // for (String role : roles) {
                    //     authorities.add(new SimpleGrantedAuthority(role));
                    // }

                    stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception exception) {
                    log.error("Error logging in: {}", exception.getMessage());
                    response.setHeader("Error", exception.getMessage());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    //response.sendError(HttpStatus.FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", exception.getMessage());
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }else{
                filterChain.doFilter(request, response);
            }
        }

    }

}
