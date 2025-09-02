package com.example.carsharing.controller;

import com.example.carsharing.dto.payment.PaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import com.example.carsharing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for managing payments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    //@PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create payment session",
            description = "Create a new Stripe payment session")
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid PaymentRequestDto requestDto) {
        return paymentService.createPaymentSession(requestDto);
    }

    @GetMapping
    //@PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER')")
    @Operation(summary = "Get payments by user ID",
            description = "Get all payments for a specific user")
    public List<PaymentResponseDto> getPayments(@RequestParam Long userId) {
        return paymentService.getPaymentsByUserId(userId);
    }

    @GetMapping("/success/{sessionId}")
    @Operation(summary = "Handle successful payment",
            description = "Stripe redirect endpoint for successful payments")
    public PaymentResponseDto handleSuccessfulPayment(@PathVariable String sessionId) {
        return paymentService.handleSuccessfulPayment(sessionId);
    }

    @GetMapping("/cancel/{sessionId}")
    @Operation(summary = "Handle cancelled payment",
            description = "Stripe redirect endpoint for cancelled payments")
    public String handleCancelledPayment(@PathVariable String sessionId) {
        return paymentService.handleCancelledPayment(sessionId);
    }
}
