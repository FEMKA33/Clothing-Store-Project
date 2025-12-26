package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.UserEntity;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
}