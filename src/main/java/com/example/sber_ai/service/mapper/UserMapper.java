package com.example.sber_ai.service.mapper;

import com.example.sber_ai.entity.User;
import com.example.sber_ai.model.response.CreateUserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    CreateUserResponse mapToResponse(User user);
}
