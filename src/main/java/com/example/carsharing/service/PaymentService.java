package com.example.carsharing.service;

import com.example.carsharing.dto.payment.PaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(PaymentRequestDto requestDto);

    List<PaymentResponseDto> getPaymentsByUserId(Long userId);

    PaymentResponseDto handleSuccessfulPayment(String sessionId);

    String handleCancelledPayment(String sessionId);
}
