package com.example.carsharing.controller;

import com.example.carsharing.dto.car.CarRequestDto;
import com.example.carsharing.dto.car.CarResponseDto;
import com.example.carsharing.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car API", description = "Operations related to cars")
@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Get all cars", description = "Get list of all cars")
    @GetMapping
    public Page<CarResponseDto> getAll(Pageable pageable) {
        return carService.getAll(pageable);
    }

    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Get car by id", description = "Get car by id")
    @GetMapping("/{id}")
    public CarResponseDto getCarById(@PathVariable Long id) {
        return carService.get(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create car", description = "Create a new car")
    @PostMapping
    public CarResponseDto createCar(@RequestBody @Valid CarRequestDto requestDto) {
        return carService.create(requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete car by id", description = "Delete car by id")
    @DeleteMapping("/{id}")
    public void deleteCarById(@PathVariable Long id) {
        carService.delete(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update car by id",description = "Update car by id")
    @PutMapping("/{id}")
    public CarResponseDto updateCarById(@RequestBody @Valid CarRequestDto requestDto,
                                @PathVariable Long id) {
        return carService.update(requestDto, id);
    }
}
