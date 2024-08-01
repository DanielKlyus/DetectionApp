package com.example.sber_ai.controller;

import com.example.sber_ai.model.request.CreateUserRequest;
import com.example.sber_ai.model.request.LoginRequest;
import com.example.sber_ai.model.response.CreateUserResponse;
import com.example.sber_ai.model.response.LoginResponse;
import com.example.sber_ai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth controller", description = "Эндпоинты для регистрации пользователя")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @Operation(summary = "Регистрация пользователя", description = "Проверяет валидность входящих параметров и создает пользователя")
    @PostMapping("/register")
    public CreateUserResponse registerUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return userService.create(createUserRequest);
    }

    @Operation(summary = "Вход пользователя", description = "Проверяет валидность входящих параметров и авторизует пользователя")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginUserRequest) {
//        userService.login(loginUserRequest);
        return ResponseEntity.ok().build();
    }
}
