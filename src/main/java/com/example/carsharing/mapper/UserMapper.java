package com.example.carsharing.mapper;

import com.example.carsharing.config.MapperConfig;
import com.example.carsharing.dto.user.UserProfileDto;
import com.example.carsharing.dto.user.UserProfilePatchDto;
import com.example.carsharing.dto.user.UserRegisterDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.model.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegisterDto registerDto);

    UserResponseDto toDto(User user);

    void updateUserFromDto(UserProfileDto dto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy =
            NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromPatchDto(UserProfilePatchDto dto,
                                @MappingTarget User user);
}
