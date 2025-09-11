package com.example.carsharing.dto.car;

import com.example.carsharing.model.car.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CarRequestDto(
        @NotBlank String model,
        @NotBlank String brand,
        @NotNull Type type,
        @Positive int inventory,
        @Positive BigDecimal dailyFee
) {
}
