package com.example.carsharing.security;

import com.example.carsharing.dto.user.UserLoginRequestDto;
import com.example.carsharing.dto.user.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto request);

    Long getCurrentUserId();
}
