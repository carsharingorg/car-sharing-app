package com.example.carsharing.service.impl;

import static com.example.carsharing.util.CarUtil.createDefaultCar;
import static com.example.carsharing.util.RentalUtil.createActiveRental;
import static com.example.carsharing.util.RentalUtil.createClosedRental;
import static com.example.carsharing.util.RentalUtil.createDefaultRental;
import static com.example.carsharing.util.RentalUtil.createDefaultRentalRequestDto;
import static com.example.carsharing.util.RentalUtil.createDefaultRentalResponseDto;
import static com.example.carsharing.util.RentalUtil.createInvalidReturnDateRentalRequestDto;
import static com.example.carsharing.util.RentalUtil.createPastDateRentalRequestDto;
import static com.example.carsharing.util.UserUtil.createDefaultUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.exception.AccessDeniedException;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.exception.IllegalArgumentException;
import com.example.carsharing.mapper.RentalMapper;
import com.example.carsharing.model.car.Car;
import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.model.user.User;
import com.example.carsharing.repository.car.CarRepository;
import com.example.carsharing.repository.rental.RentalRepository;
import com.example.carsharing.repository.user.UserRepository;
import com.example.carsharing.telegram.notification.NotificationService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    void addRental_ValidData_ShouldReturnRentalResponseDto() {
        long currentUserId = 1;
        RentalRequestDto requestDto = createDefaultRentalRequestDto();
        Car car = createDefaultCar();
        car.setInventory(5);
        User user = createDefaultUser();
        Rental rental = createDefaultRental();
        RentalResponseDto expectedDto = createDefaultRentalResponseDto();

        when(carRepository.findById(requestDto.carId())).thenReturn(Optional.of(car));
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(user));
        when(rentalMapper.toModel(requestDto)).thenReturn(rental);
        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expectedDto);
        doNothing().when(notificationService)
                .sendRentalCreatedNotification(rental, user, car.getModel());

        RentalResponseDto actualDto =
                rentalService.addRental(currentUserId, requestDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        assertEquals(4, car.getInventory());
        verify(carRepository).findById(requestDto.carId());
        verify(userRepository).findById(currentUserId);
        verify(rentalMapper).toModel(requestDto);
        verify(carRepository).save(car);
        verify(rentalRepository).save(rental);
        verify(rentalMapper).toDto(rental);
        verify(notificationService).sendRentalCreatedNotification(rental, user, car.getModel());
    }

    @Test
    void addRental_PastRentalDate_ShouldThrowIllegalArgumentException() {
        long currentUserId = 1;
        RentalRequestDto requestDto = createPastDateRentalRequestDto();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.addRental(currentUserId, requestDto)
        );

        assertTrue(exception.getMessage().contains("Rental date cannot be in the past"));
        verify(carRepository, never()).findById(any());
    }

    @Test
    void addRental_ReturnDateBeforeRentalDate_ShouldThrowIllegalArgumentException() {
        Long currentUserId = 1L;
        RentalRequestDto requestDto = createInvalidReturnDateRentalRequestDto();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.addRental(currentUserId, requestDto)
        );

        assertTrue(exception.getMessage().contains("Return date cannot be before rental date"));
        verify(carRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void addRental_CarNotFound_ShouldThrowEntityNotFoundException() {
        Long currentUserId = 1L;
        RentalRequestDto requestDto = createDefaultRentalRequestDto();

        when(carRepository.findById(requestDto.carId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.addRental(currentUserId, requestDto)
        );

        assertEquals("Can't find Car with id 1", exception.getMessage());
        verify(carRepository).findById(requestDto.carId());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void addRental_NoInventoryAvailable_ShouldThrowIllegalArgumentException() {
        Long currentUserId = 1L;
        RentalRequestDto requestDto = createDefaultRentalRequestDto();
        Car car = createDefaultCar();
        car.setInventory(0);

        when(carRepository.findById(requestDto.carId())).thenReturn(Optional.of(car));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.addRental(currentUserId, requestDto)
        );

        assertEquals("No cars available for rental (inventory = 0)",
                exception.getMessage());
        verify(carRepository).findById(requestDto.carId());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void addRental_UserNotFound_ShouldThrowEntityNotFoundException() {
        Long currentUserId = 999L;
        RentalRequestDto requestDto = createDefaultRentalRequestDto();
        Car car = createDefaultCar();
        car.setInventory(5);

        when(carRepository.findById(requestDto.carId())).thenReturn(Optional.of(car));
        when(userRepository.findById(currentUserId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.addRental(currentUserId, requestDto)
        );

        assertEquals("Can't find User with id + 999", exception.getMessage());
        verify(carRepository).findById(requestDto.carId());
        verify(userRepository).findById(currentUserId);
    }

    @Test
    void get_ExistingId_ShouldReturnRentalResponseDto() {
        long existedId = 1L;
        Rental rental = createDefaultRental();
        RentalResponseDto expectedResponseDto = createDefaultRentalResponseDto();

        when(rentalRepository.findById(existedId)).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(expectedResponseDto);

        RentalResponseDto actualRentalResponseDto = rentalService.get(existedId);
        assertEquals(expectedResponseDto, actualRentalResponseDto);
        verify(rentalRepository).findById(existedId);
    }

    @Test
    void get_NonExistentId_ShouldThrowEntityNotFoundException() {
        Long nonExistentId = 999L;

        when(rentalRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.get(nonExistentId)
        );

        assertEquals("Can't find Rental with id 999", exception.getMessage());
        verify(rentalRepository).findById(nonExistentId);
        verify(rentalMapper, never()).toDto(any());
    }

    @Test
    void returnRental_ValidData_ShouldReturnRentalResponseDto() {
        Car car = createDefaultCar();
        car.setInventory(3);
        Rental rental = createDefaultRental();
        rental.setCar(car);
        long rentalId = 1L;
        long currentUserId = 1L;
        rental.getUser().setId(currentUserId);
        RentalResponseDto expectedDto = createDefaultRentalResponseDto();

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(rental)).thenReturn(rental);
        doNothing().when(notificationService)
                .sendRentalReturnedNotification(rental, car.getModel());
        when(rentalMapper.toDto(rental)).thenReturn(expectedDto);

        RentalResponseDto actualDto = rentalService.returnRental(rentalId, currentUserId);

        assertEquals(expectedDto, actualDto);
        assertNotNull(actualDto);
        assertEquals(4, car.getInventory());
        assertNotNull(rental.getActualReturnDate());
        verify(rentalRepository).findById(rentalId);
        verify(carRepository).save(car);
        verify(rentalRepository).save(rental);
        verify(rentalMapper).toDto(rental);
        verify(notificationService).sendRentalReturnedNotification(rental, car.getModel());
    }

    @Test
    void returnRental_RentalNotFound_ShouldThrowEntityNotFoundException() {
        long currentUserId = 1L;
        long rentalId = 999L;

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.returnRental(rentalId, currentUserId)
        );

        assertEquals("Can't find Rental with id 999", exception.getMessage());
        verify(rentalRepository).findById(rentalId);
    }

    @Test
    void returnRental_AccessDenied_ShouldThrowAccessDeniedException() {
        long currentUserId = 1L;
        long nonExistentId = 2L;
        long rentalId = 1L;
        Rental rental = createActiveRental();
        rental.getUser().setId(nonExistentId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> rentalService.returnRental(rentalId, currentUserId)
        );

        assertEquals("You can't return car, because you didn't rental it",
                exception.getMessage());
        verify(rentalRepository).findById(rentalId);
        verify(carRepository, never()).save(any());
    }

    @Test
    void returnRental_AlreadyClosed_ShouldThrowIllegalStateException() {
        long rentalId = 1L;
        long currentUserId = 1L;
        Rental rental = createClosedRental();

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> rentalService.returnRental(rentalId, currentUserId)
        );

        assertEquals("This rental has already been closed",
                exception.getMessage());
        verify(rentalRepository).findById(rentalId);
        verify(carRepository, never()).save(any());
    }

    @Test
    void findAllByUserIdAndIsActive_EmptyList_ShouldReturnEmptyList() {
        Long userId = 1L;
        boolean isActive = true;

        when(rentalRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<RentalResponseDto> actualDto =
                rentalService.findAllByUserIdAndIsActive(userId, isActive);

        assertNotNull(actualDto);
        assertTrue(actualDto.isEmpty());
        verify(rentalRepository).findAllByUserId(userId);
        verify(rentalMapper, never()).toDto(any());
    }
}
