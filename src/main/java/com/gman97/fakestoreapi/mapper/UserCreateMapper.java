package com.gman97.fakestoreapi.mapper;

import com.gman97.fakestoreapi.dto.UserCreateDto;
import com.gman97.fakestoreapi.entity.Role;
import com.gman97.fakestoreapi.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<UserCreateDto, User> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(UserCreateDto object) {
        return new User(
                null,
                object.getUsername(),
                passwordEncoder.encode(object.getRowPassword()),
                Role.USER
        );
    }
}
