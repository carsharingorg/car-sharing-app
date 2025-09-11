package com.example.carsharing.service.impl;

import static com.example.carsharing.util.CarUtil.createDefaultCar;
import static com.example.carsharing.util.CarUtil.createDefaultCarRequestDto;
import static com.example.carsharing.util.CarUtil.createDefaultCarResponseDto;
import static com.example.carsharing.util.CarUtil.createUpdatedDefaultCarRequestDto;
import static com.example.carsharing.util.CarUtil.createUpdatedDefaultCarResponseDto;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharing.dto.car.CarRequestDto;
import com.example.carsharing.dto.car.CarResponseDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.mapper.CarMapper;
import com.example.carsharing.model.car.Car;
import com.example.carsharing.repository.car.CarRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void getAll_ShouldReturnPageOfBooks() {
        Pageable pageable = PageRequest.of(0, 20);
        Car car = createDefaultCar();
        CarResponseDto responseDto = createDefaultCarResponseDto();
        Page<CarResponseDto> expected = new PageImpl<>(List.of(responseDto),
                pageable, 1L);
        Page<Car> carPage = new PageImpl<>(List.of(car),
                pageable, 1L);
        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(car)).thenReturn(responseDto);

        Page<CarResponseDto> actual = carService.getAll(pageable);
        assertEquals(expected, actual);
    }

    @Test
    void get_ExistId_ShouldReturnCarResponseDto() {
        long existedId = 1;
        Car car = createDefaultCar();
        CarResponseDto expectedDto = createDefaultCarResponseDto();

        when(carRepository.findById(existedId)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(expectedDto);

        CarResponseDto actualDto = carService.get(existedId);

        verify(carRepository).findById(existedId);
        verify(carMapper).toDto(car);
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void get_NonExistentId_ShouldThrowEntityNotFoundException() {
        long nonExistentId = 999L;

        when(carRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.get(nonExistentId)
        );

        assertEquals("Car with id 999 not found", exception.getMessage());
    }

    @Test
    void create_ValidInputData_ShouldReturnCarResponseDto() {
        CarRequestDto carRequestDto = createDefaultCarRequestDto();
        Car car = createDefaultCar();
        CarResponseDto carResponseDto = createDefaultCarResponseDto();

        when(carMapper.toModel(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        CarResponseDto actualDto = carService.create(carRequestDto);

        assertNotNull(actualDto);
        assertEquals(carResponseDto, actualDto);
        verify(carMapper).toModel(carRequestDto);
        verify(carRepository).save(car);
        verify(carMapper).toDto(car);
    }

    @Test
    void update_ValidInputData_ShouldReturnCarResponseDto() {
        long existedId = 1L;
        Car car = createDefaultCar();
        CarRequestDto updatedDto = createUpdatedDefaultCarRequestDto();
        CarResponseDto updatedCarResponseDto = createUpdatedDefaultCarResponseDto();

        when(carRepository.findById(existedId)).thenReturn(Optional.of(car));
        doNothing().when(carMapper).updateCarFromDto(updatedDto, car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(updatedCarResponseDto);

        CarResponseDto actual = carService.update(updatedDto, existedId);

        assertNotNull(actual);
        assertEquals(updatedCarResponseDto, actual);
        verify(carRepository).findById(existedId);
        verify(carMapper).updateCarFromDto(updatedDto, car);
        verify(carRepository).save(car);
        verify(carMapper).toDto(car);
    }

    @Test
    void update_NonExistentId_ShouldThrowEntityNotFoundException() {
        long nonExistentId = 999L;
        CarRequestDto updatedDto = createUpdatedDefaultCarRequestDto();

        when(carRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.update(updatedDto, nonExistentId)
        );

        assertEquals("Car with id 999 not found", exception.getMessage());
        verify(carRepository).findById(nonExistentId);
        verify(carMapper, never()).updateCarFromDto(any(), any());
        verify(carRepository, never()).save(any());
    }

    @Test
    void delete_ExistingId_ShouldDeleteCar() {
        long existingId = 1L;

        when(carRepository.existsById(existingId)).thenReturn(true);
        doNothing().when(carRepository).deleteById(existingId);

        assertDoesNotThrow(() -> carService.delete(existingId));

        verify(carRepository).existsById(existingId);
        verify(carRepository).deleteById(existingId);
    }

    @Test
    void delete_NonExistentId_ShouldThrowEntityNotFoundException() {
        long nonExistentId = 999L;

        when(carRepository.existsById(nonExistentId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.delete(nonExistentId)
        );

        assertEquals("Car with id 999 not found", exception.getMessage());
        verify(carRepository).existsById(nonExistentId);
        verify(carRepository, never()).deleteById(nonExistentId);
    }
}
