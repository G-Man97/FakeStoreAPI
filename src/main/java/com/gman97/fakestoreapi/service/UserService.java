package com.gman97.fakestoreapi.service;

import com.gman97.fakestoreapi.dto.UserCreateDto;
import com.gman97.fakestoreapi.mapper.UserCreateMapper;
import com.gman97.fakestoreapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserCreateMapper userCreateMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singleton(user.getRole())
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }

    @Transactional
    public void create(UserCreateDto userCreateDto) {
        var user = userCreateMapper.map(userCreateDto);
        userRepository.save(user);
    }
}
