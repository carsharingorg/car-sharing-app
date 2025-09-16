package com.example.carsharing.service;

import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import java.util.List;

public interface RentalService {
    RentalResponseDto addRental(Long currentUserId, RentalRequestDto requestDto);

    RentalResponseDto get(Long id);

    RentalResponseDto returnRental(Long id, Long currentUserId);

    List<RentalResponseDto> findAllByUserIdAndIsActive(Long userId, boolean isActive);
}
