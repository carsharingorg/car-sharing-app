package com.example.carsharing.service;

import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.dto.rental.RentalSearchParametersDto;
import java.util.List;

public interface RentalService {
    List<RentalResponseDto> search(RentalSearchParametersDto searchParameters);

    RentalResponseDto addRental(Long currentUserId, RentalRequestDto requestDto);

    RentalResponseDto get(Long id);

    RentalResponseDto returnRental(Long id, Long currentUserId);
}
