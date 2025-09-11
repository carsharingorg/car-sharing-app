package com.example.carsharing.dto.car;

import com.example.carsharing.model.car.Type;
import java.math.BigDecimal;

public record CarResponseDto(
        Long id,
        String model,
        String brand,
        Type type,
        BigDecimal dailyFee
) {
}
