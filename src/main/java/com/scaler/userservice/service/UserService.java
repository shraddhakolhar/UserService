package com.scaler.userservice.service;

import com.scaler.userservice.dto.UserResponse;
import com.scaler.userservice.entity.UserEntity;
import com.scaler.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getProfile(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public UserResponse updateProfile(
            String email, String firstName, String lastName) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found"));

        user.setFirstName(firstName);
        user.setLastName(lastName);

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
