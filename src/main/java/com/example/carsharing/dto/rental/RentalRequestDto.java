package com.example.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record RentalRequestDto(
        @NotNull LocalDate rentalDate,
        @NotNull LocalDate returnDate,
        @Positive @NotNull Long carId
) {
}
