package com.example.carsharing.service.impl;

import static com.example.carsharing.util.TestUtil.createDefaultRole;
import static com.example.carsharing.util.TestUtil.createDefaultUser;
import static com.example.carsharing.util.TestUtil.createDefaultUserProfileDto;
import static com.example.carsharing.util.TestUtil.createDefaultUserProfilePatchDto;
import static com.example.carsharing.util.TestUtil.createDefaultUserRegisterDto;
import static com.example.carsharing.util.TestUtil.createDefaultUserResponseDto;
import static com.example.carsharing.util.TestUtil.createManagerRole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharing.dto.user.UserProfileDto;
import com.example.carsharing.dto.user.UserProfilePatchDto;
import com.example.carsharing.dto.user.UserRegisterDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.exception.RegistrationException;
import com.example.carsharing.mapper.UserMapper;
import com.example.carsharing.model.user.Role;
import com.example.carsharing.model.user.RoleName;
import com.example.carsharing.model.user.User;
import com.example.carsharing.repository.user.RoleRepository;
import com.example.carsharing.repository.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_ValidData_ShouldReturnUserResponseDto() throws RegistrationException {
        UserRegisterDto registerDto = createDefaultUserRegisterDto();
        User user = createDefaultUser();
        Role userRole = createDefaultRole();
        UserResponseDto expectedDto = createDefaultUserResponseDto();
        String encodedPassword = "encodedPassword123";

        when(userRepository.existsByEmail(registerDto.email())).thenReturn(false);
        when(userMapper.toModel(registerDto)).thenReturn(user);
        when(passwordEncoder.encode(registerDto.password())).thenReturn(encodedPassword);
        when(roleRepository.findRoleByName(RoleName.ROLE_CUSTOMER))
                .thenReturn(Optional.of(userRole));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserResponseDto actualDto = userService.register(registerDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userRepository).existsByEmail(registerDto.email());
        verify(userMapper).toModel(registerDto);
        verify(passwordEncoder).encode(registerDto.password());
        verify(roleRepository).findRoleByName(RoleName.ROLE_CUSTOMER);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void register_ExistingEmail_ShouldThrowRegistrationException() {
        UserRegisterDto registerDto = createDefaultUserRegisterDto();

        when(userRepository.existsByEmail(registerDto.email())).thenReturn(true);

        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(registerDto)
        );

        assertEquals("Email test@example.com has been already used",
                exception.getMessage());
        verify(userRepository).existsByEmail(registerDto.email());
        verify(userMapper, never()).toModel(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_RoleNotFound_ShouldThrowEntityNotFoundException() {
        UserRegisterDto registerDto = createDefaultUserRegisterDto();
        User user = createDefaultUser();
        String encodedPassword = "encodedPassword123";

        when(userRepository.existsByEmail(registerDto.email())).thenReturn(false);
        when(userMapper.toModel(registerDto)).thenReturn(user);
        when(passwordEncoder.encode(registerDto.password())).thenReturn(encodedPassword);
        when(roleRepository.findRoleByName(RoleName.ROLE_CUSTOMER)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.register(registerDto)
        );

        assertTrue(exception.getMessage().contains("Can't find role for user"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserRole_ValidData_ShouldReturnUserResponseDto() {
        Long userId = 1L;
        String roleName = "ROLE_MANAGER";
        User user = createDefaultUser();
        Role role = createManagerRole();
        UserResponseDto expectedDto = createDefaultUserResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findRoleByName(RoleName.valueOf(roleName)))
                .thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserResponseDto actualDto = userService.updateUserRole(userId, roleName);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userRepository).findById(userId);
        verify(roleRepository).findRoleByName(RoleName.valueOf(roleName));
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void updateUserRole_UserNotFound_ShouldThrowEntityNotFoundException() {
        Long nonExistentUserId = 999L;
        String roleName = "ROLE_MANAGER";

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUserRole(nonExistentUserId, roleName)
        );

        assertEquals("User not exist with id 999", exception.getMessage());
        verify(userRepository).findById(nonExistentUserId);
        verify(roleRepository, never()).findRoleByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getProfileInfo_ExistingUser_ShouldReturnUserResponseDto() {
        Long userId = 1L;
        User user = createDefaultUser();
        UserResponseDto expectedDto = createDefaultUserResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserResponseDto actualDto = userService.getProfileInfo(userId);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(user);
    }

    @Test
    void getProfileInfo_UserNotFound_ShouldThrowEntityNotFoundException() {
        Long nonExistentUserId = 999L;

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getProfileInfo(nonExistentUserId)
        );

        assertEquals("User not exist with id 999", exception.getMessage());
        verify(userRepository).findById(nonExistentUserId);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void updateUserPartially_ValidData_ShouldReturnUserResponseDto() {
        Long userId = 1L;
        UserProfilePatchDto patchDto = createDefaultUserProfilePatchDto();
        User user = createDefaultUser();
        UserResponseDto expectedDto = createDefaultUserResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserFromPatchDto(patchDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserResponseDto actualDto = userService.updateUserPartially(userId, patchDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userRepository).findById(userId);
        verify(userMapper).updateUserFromPatchDto(patchDto, user);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void updateUserPartially_UserNotFound_ShouldThrowEntityNotFoundException() {
        Long nonExistentUserId = 999L;
        UserProfilePatchDto patchDto = createDefaultUserProfilePatchDto();

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUserPartially(nonExistentUserId, patchDto)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(nonExistentUserId);
        verify(userMapper, never()).updateUserFromPatchDto(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserFully_ValidData_ShouldReturnUserResponseDto() {
        Long userId = 1L;
        UserProfileDto profileDto = createDefaultUserProfileDto();
        User user = createDefaultUser();
        UserResponseDto expectedDto = createDefaultUserResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserFromDto(profileDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserResponseDto actualDto = userService.updateUserFully(userId, profileDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userRepository).findById(userId);
        verify(userMapper).updateUserFromDto(profileDto, user);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void updateUserFully_UserNotFound_ShouldThrowEntityNotFoundException() {
        Long nonExistentUserId = 999L;
        UserProfileDto profileDto = createDefaultUserProfileDto();

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUserFully(nonExistentUserId, profileDto)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(nonExistentUserId);
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userRepository, never()).save(any());
    }
}
