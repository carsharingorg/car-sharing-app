package com.example.carsharing.dto.payment;

public record PaymentCancelResponse(
        String status,
        String message,
        String sessionId,
        String reminder
) {
}
