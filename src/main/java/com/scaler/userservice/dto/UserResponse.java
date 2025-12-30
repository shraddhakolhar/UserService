package com.scaler.userservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
