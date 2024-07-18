package com.example.sber_ai.service;

import com.example.sber_ai.model.request.UserRequest;
import com.example.sber_ai.model.response.UserResponse;

public interface UserService {
    public UserResponse createUser(UserRequest userRequest);
}
