package com.example.carsharing.service.impl;

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
import com.example.carsharing.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegisterDto registerDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(registerDto.email())) {
            throw new RegistrationException(String.format("Email %s has been already used",
                    registerDto.email()));
        }
        User user = userMapper.toModel(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.password()));
        Role userRole = roleRepository.findRoleByName(RoleName.ROLE_CUSTOMER).orElseThrow(
                () -> new EntityNotFoundException("Can't find role for user: "
                        + user.getUsername() + ", role: " + RoleName.ROLE_CUSTOMER));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        return null;
    }

    @Override
    public UserResponseDto updateUserRole(Long id, String role) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not exist with id " + id));
        Role roleName = roleRepository.findRoleByName(RoleName.valueOf(role)).orElseThrow(
                () -> new EntityNotFoundException("Role doesn't exist with name:" + role));
        user.setRoles(Set.of(roleName));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getProfileInfo(Long currentUserId) {
        User user = userRepository.findById(currentUserId).orElseThrow(
                () -> new EntityNotFoundException("User not exist with id " + currentUserId));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateUserPartially(Long currentUserId, UserProfilePatchDto patchDto) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userMapper.updateUserFromPatchDto(patchDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateUserFully(Long currentUserId, UserProfileDto userProfileDto) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userMapper.updateUserFromDto(userProfileDto, user);
        return userMapper.toDto(userRepository.save(user));
    }
}
