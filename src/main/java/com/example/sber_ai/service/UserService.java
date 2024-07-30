package com.example.sber_ai.service;

import com.example.sber_ai.model.request.CreateUserRequest;
import com.example.sber_ai.model.response.CreateUserResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    CreateUserResponse create(CreateUserRequest createUserRequest);

    UserDetails loadUserByUsername(String username);
}
