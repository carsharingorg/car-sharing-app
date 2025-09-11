package com.example.carsharing.dto.payment;

import com.example.carsharing.model.payment.Status;
import com.example.carsharing.model.payment.Type;
import java.math.BigDecimal;

public record PaymentResponseDto(
        Long id,
        Status status,
        Type type,
        Long rentalId,
        String sessionUrl,
        String sessionId,
        BigDecimal amountToPay
) {}
