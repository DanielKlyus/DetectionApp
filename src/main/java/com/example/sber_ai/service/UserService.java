package com.example.sber_ai.service;

import com.example.sber_ai.model.request.CreateUserRequest;
import com.example.sber_ai.model.response.CreateUserResponse;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest createUserRequest);
}
