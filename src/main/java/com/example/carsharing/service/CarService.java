package com.example.carsharing.service;

import com.example.carsharing.dto.car.CarRequestDto;
import com.example.carsharing.dto.car.CarResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    Page<CarResponseDto> getAll(Pageable pageable);

    CarResponseDto get(Long id);

    void delete(Long id);

    CarResponseDto update(CarRequestDto requestDto, Long id);

    CarResponseDto create(CarRequestDto requestDto);
}
