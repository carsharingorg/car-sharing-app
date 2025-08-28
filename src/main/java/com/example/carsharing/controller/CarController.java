package com.example.carsharing.controller;

import com.example.carsharing.dto.car.CarRequestDto;
import com.example.carsharing.dto.car.CarResponseDto;
import com.example.carsharing.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping
    public Page<CarResponseDto> getAll(Pageable pageable) {
        return carService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public CarResponseDto getCarById(@PathVariable Long id) {
        return carService.get(id);
    }

    @PostMapping
    public CarResponseDto createCar(@RequestBody @Valid CarRequestDto requestDto) {
        return carService.create(requestDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCarById(@PathVariable Long id) {
        carService.delete(id);
    }

    @PutMapping("/{id}")
    public CarResponseDto updateCarById(@RequestBody @Valid CarRequestDto requestDto,
                                @PathVariable Long id) {
        return carService.update(requestDto, id);
    }
}
