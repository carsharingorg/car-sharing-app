package com.example.carsharing.util;

import com.example.carsharing.dto.user.UserProfileDto;
import com.example.carsharing.dto.user.UserProfilePatchDto;
import com.example.carsharing.dto.user.UserRegisterDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.model.user.Role;
import com.example.carsharing.model.user.RoleName;
import com.example.carsharing.model.user.User;
import java.util.Set;

public class UserUtil {

    private UserUtil() {
    }

    public static User createDefaultUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setRoles(Set.of(createDefaultRole()));
        user.setDeleted(false);
        return user;
    }

    public static Role createDefaultRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_CUSTOMER);
        return role;
    }

    public static Role createManagerRole() {
        Role role = new Role();
        role.setId(2L);
        role.setName(RoleName.ROLE_MANAGER);
        return role;
    }

    public static UserRegisterDto createDefaultUserRegisterDto() {
        return new UserRegisterDto(
                "test@example.com",
                "password123",
                "password123",
                "John",
                "Doe"
        );
    }

    public static UserResponseDto createDefaultUserResponseDto() {
        return new UserResponseDto(
                1L,
                "test@example.com",
                "John",
                "Doe"
        );
    }

    public static UserProfileDto createDefaultUserProfileDto() {
        return new UserProfileDto(
                "updated@example.com",
                "Jane",
                "Smith"
        );
    }

    public static UserProfilePatchDto createDefaultUserProfilePatchDto() {
        return new UserProfilePatchDto(
                "patched@example.com",
                "Updated John",
                "Updated Doe"
        );
    }

}
