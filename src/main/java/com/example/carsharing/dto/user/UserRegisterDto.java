package com.example.carsharing.dto.user;

import com.example.carsharing.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@FieldMatch(first = "password", second = "repeatPassword", message = "Passwords don't match")
public record UserRegisterDto(
        @NotBlank @Email @Length(min = 10, max = 30) String email,
        @NotBlank @Length(min = 10, max = 30) String password,
        @NotBlank String repeatPassword,
        @NotBlank String firstName,
        @NotBlank String lastName
) {
}
