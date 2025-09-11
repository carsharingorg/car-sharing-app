package com.example.carsharing.dto.payment;

public record PaymentSuccessResponse(
        String status,
        String message,
        String sessionId,
        Long paymentId,
        Long amount
) {
}
