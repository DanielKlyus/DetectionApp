package com.example.sber_ai.service.impl;

import com.example.sber_ai.model.entity.User;
import com.example.sber_ai.model.request.CreateUserRequest;
import com.example.sber_ai.model.response.CreateUserResponse;
import com.example.sber_ai.repository.UserRepository;
import com.example.sber_ai.security.JwtUtil;
import com.example.sber_ai.security.model.Role;
import com.example.sber_ai.service.UserService;
import com.example.sber_ai.service.mapper.UserMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Setter
@Getter
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final JwtUtil jwtUtil;

    @Override
    public CreateUserResponse create(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getName());
        user.setPassword(createUserRequest.getPassword());
        user.setEmail(createUserRequest.getEmail());
        user.setRoles(new ArrayList<>(List.of(new Role("ROLE_USER"))));
        user.setAdmin(false);
        userRepository.save(user);

        return new CreateUserResponse(jwtUtil.generateToken(user.getId()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findById(Long.valueOf(username));
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return new org.springframework.security.core.userdetails.User(String.valueOf(user.get().getId()), user.get().getPassword(), getGrantedAuthorities(user.get().getRoles()));
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return authorities;
    }

}
