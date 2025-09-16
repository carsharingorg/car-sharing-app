package com.example.carsharing.dto.payment;

import com.example.carsharing.model.payment.Type;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequestDto(
        @NotNull(message = "Rental ID is required")
        @Positive
        Long rentalId,

        @NotNull(message = "Payment type is required")
        Type type
) {
}
