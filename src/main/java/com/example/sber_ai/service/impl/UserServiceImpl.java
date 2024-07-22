package com.example.sber_ai.service.impl;

import com.example.sber_ai.entity.User;
import com.example.sber_ai.model.request.CreateUserRequest;
import com.example.sber_ai.model.response.CreateUserResponse;
import com.example.sber_ai.repository.UserRepository;
import com.example.sber_ai.service.UserService;
import com.example.sber_ai.service.mapper.UserMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Setter
@Getter
public class UserServiceImpl  implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setName(createUserRequest.getName());
        user.setAdmin(false);
        userRepository.save(user);

        return userMapper.mapToResponse(user);
    }
}
