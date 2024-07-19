package com.example.sber_ai.service;

import com.example.sber_ai.model.request.CreateUserRequest;
import com.example.sber_ai.model.response.CreateUserResponse;

public interface UserService {
    public CreateUserResponse createUser(CreateUserRequest createUserRequest);
}
