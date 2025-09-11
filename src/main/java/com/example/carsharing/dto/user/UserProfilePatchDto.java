package com.example.carsharing.dto.user;

import jakarta.validation.constraints.Email;

public record UserProfilePatchDto(
        @Email String email,
        String firstName,
        String lastName
) {
}
