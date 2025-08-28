package com.example.carsharing.service.impl;

import com.example.carsharing.dto.car.CarRequestDto;
import com.example.carsharing.dto.car.CarResponseDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.mapper.CarMapper;
import com.example.carsharing.model.car.Car;
import com.example.carsharing.repository.car.CarRepository;
import com.example.carsharing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public Page<CarResponseDto> getAll(Pageable pageable) {
        return carRepository.findAll(pageable).map(carMapper::toDto);
    }

    @Override
    public CarResponseDto get(Long id) {
        return carMapper.toDto(carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Car with id " + id
                        + " not found")));
    }

    @Override
    public CarResponseDto create(CarRequestDto requestDto) {
        Car savedCar = carRepository.save(carMapper.toModel(requestDto));
        return carMapper.toDto(savedCar);
    }

    @Override
    public CarResponseDto update(CarRequestDto requestDto, Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Car with id " + id
                        + " not found"));
        carMapper.updateCarFromDto(requestDto, car);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public void delete(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Car with id " + id
                    + " not found");
        }
        carRepository.deleteById(id);
    }
}
