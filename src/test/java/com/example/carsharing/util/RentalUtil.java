package com.example.carsharing.util;

import static com.example.carsharing.util.CarUtil.createDefaultCar;
import static com.example.carsharing.util.UserUtil.createDefaultUser;

import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.model.rental.Rental;
import java.time.LocalDate;

public class RentalUtil {
    private RentalUtil() {
    }

    public static Rental createDefaultRental() {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.now().plusDays(1));
        rental.setReturnDate(LocalDate.now().plusDays(5));
        rental.setActualReturnDate(null);
        rental.setCar(createDefaultCar());
        rental.setUser(createDefaultUser());
        return rental;
    }

    public static Rental createActiveRental() {
        Rental rental = createDefaultRental();
        rental.setActualReturnDate(null);
        return rental;
    }

    public static Rental createClosedRental() {
        Rental rental = createDefaultRental();
        rental.setActualReturnDate(LocalDate.now());
        return rental;
    }

    public static RentalRequestDto createDefaultRentalRequestDto() {
        return new RentalRequestDto(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                1L
        );
    }

    public static RentalRequestDto createPastDateRentalRequestDto() {
        return new RentalRequestDto(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5),
                1L
        );
    }

    public static RentalRequestDto createInvalidReturnDateRentalRequestDto() {
        return new RentalRequestDto(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(3),
                1L
        );
    }

    public static RentalResponseDto createDefaultRentalResponseDto() {
        return new RentalResponseDto(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                null,
                1L,
                1L
        );
    }
}
