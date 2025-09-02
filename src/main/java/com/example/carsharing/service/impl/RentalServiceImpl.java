package com.example.carsharing.service.impl;

import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.exception.AccessDeniedException;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.mapper.RentalMapper;
import com.example.carsharing.model.car.Car;
import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.model.user.User;
import com.example.carsharing.repository.car.CarRepository;
import com.example.carsharing.repository.rental.RentalRepository;
import com.example.carsharing.repository.user.UserRepository;
import com.example.carsharing.service.RentalService;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public RentalResponseDto addRental(Long currentUserId, RentalRequestDto requestDto) {
        LocalDate today = LocalDate.now();
        if (requestDto.rentalDate().isBefore(today)) {
            throw new IllegalArgumentException("Rental date cannot be in the past: "
                    + requestDto.rentalDate());
        }
        if (requestDto.returnDate().isBefore(requestDto.rentalDate())) {
            throw new IllegalArgumentException("Return date cannot be before rental date: "
                    + requestDto.returnDate());
        }
        Car car = carRepository.findById(requestDto.carId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find Car with id " + requestDto.carId()));
        if (car.getInventory() <= 0) {
            throw new IllegalArgumentException("No cars available for rental (inventory = 0)");
        }
        User user = userRepository.findById(currentUserId).orElseThrow(
                () -> new EntityNotFoundException("Can't find User with id + " + currentUserId));
        Rental rental = rentalMapper.toModel(requestDto);
        rental.setCar(car);
        rental.setUser(user);
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public RentalResponseDto get(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find Rental with id " + id));
        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalResponseDto returnRental(Long id, Long currentUserId) {
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find Rental with id " + id));
        if (!Objects.equals(rental.getUser().getId(), currentUserId)) {
            throw new AccessDeniedException("You can't return car, because you didn't rental it");
        }
        if (rental.getActualReturnDate() != null) {
            throw new IllegalStateException("This rental has already been closed");
        }
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        rental.setActualReturnDate(LocalDate.now());
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalResponseDto> findAllByUserIdAndIsActive(Long userId, boolean isActive) {
        List<Rental> rentals = rentalRepository.findAllByUserId(userId);
        return rentals.stream()
                .filter(r -> r.isActive() == isActive)
                .map(rentalMapper::toDto)
                .toList();
    }
}
