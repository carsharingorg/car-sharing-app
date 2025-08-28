package com.example.carsharing.controller;

import com.example.carsharing.dto.user.UserProfileDto;
import com.example.carsharing.dto.user.UserProfilePatchDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.security.AuthenticationService;
import com.example.carsharing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PutMapping("/{id}/role")
    public UserResponseDto updateUserRole(@PathVariable Long id, String role) {
        return userService.updateUserRole(id, role);
    }

    @GetMapping("/me")
    public UserResponseDto getProfile() {
        return userService.getProfileInfo(authenticationService.getCurrentUserId());
    }

    @PatchMapping("/me")
    public UserResponseDto updateProfilePatch(@RequestBody UserProfilePatchDto patchDto) {
        return userService.updateUserPartially(authenticationService.getCurrentUserId(),
                patchDto);
    }

    @PutMapping("/me")
    public UserResponseDto updateProfilePut(
            @Valid @RequestBody UserProfileDto userProfileDto) {
        return userService.updateUserFully(authenticationService.getCurrentUserId(),
                userProfileDto);
    }
}
