package com.example.carsharing.service;

import com.example.carsharing.dto.user.UserProfileDto;
import com.example.carsharing.dto.user.UserProfilePatchDto;
import com.example.carsharing.dto.user.UserRegisterDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegisterDto registerDto)
            throws RegistrationException;

    UserResponseDto updateUserRole(Long id, String role);

    UserResponseDto getProfileInfo(Long currentUserId);

    UserResponseDto updateUserPartially(Long currentUserId, UserProfilePatchDto patchDto);

    UserResponseDto updateUserFully(Long currentUserId, UserProfileDto userProfileDto);
}
