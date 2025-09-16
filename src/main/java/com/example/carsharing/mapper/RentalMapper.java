package com.example.carsharing.mapper;

import com.example.carsharing.config.MapperConfig;
import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.model.rental.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "user.id", target = "userId")
    RentalResponseDto toDto(Rental rental);

    @Mapping(target = "car", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "actualReturnDate", ignore = true)
    Rental toModel(RentalRequestDto requestDto);
}
