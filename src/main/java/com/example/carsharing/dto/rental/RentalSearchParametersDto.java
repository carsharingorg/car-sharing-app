package com.example.carsharing.dto.rental;

public record RentalSearchParametersDto(
        String[] userId,
        String[] isActive
) {
}
