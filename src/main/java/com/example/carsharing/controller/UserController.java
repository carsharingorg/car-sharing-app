package com.example.carsharing.controller;

import com.example.carsharing.dto.user.UserProfileDto;
import com.example.carsharing.dto.user.UserProfilePatchDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.security.AuthenticationService;
import com.example.carsharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "Operations related to user profiles and roles")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update user role",
            description = "Update role of a user by user id")
    @PutMapping("/{id}/role")
    public UserResponseDto updateUserRole(@PathVariable Long id, String role) {
        return userService.updateUserRole(id, role);
    }

    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Get current user profile",
            description = "Get profile information of the currently authenticated user")
    @GetMapping("/me")
    public UserResponseDto getProfile() {
        return userService.getProfileInfo(authenticationService.getCurrentUserId());
    }

    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Partially update current user profile",
            description = "Update specific fields of the current user's profile")
    @PatchMapping("/me")
    public UserResponseDto updateProfilePatch(@RequestBody UserProfilePatchDto patchDto) {
        return userService.updateUserPartially(authenticationService.getCurrentUserId(),
                patchDto);
    }

    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Fully update current user profile",
            description = "Update the entire profile of the currently authenticated user")
    @PutMapping("/me")
    public UserResponseDto updateProfilePut(
            @Valid @RequestBody UserProfileDto userProfileDto) {
        return userService.updateUserFully(authenticationService.getCurrentUserId(),
                userProfileDto);
    }
}
