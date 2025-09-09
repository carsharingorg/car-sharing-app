package com.example.carsharing.util;

import static com.example.carsharing.util.TestUtil.createDefaultRental;
import static com.example.carsharing.util.TestUtil.createDefaultUser;
import static com.example.carsharing.util.TestUtil.createSecondDefaultCar;

import com.example.carsharing.dto.payment.PaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import com.example.carsharing.model.payment.Payment;
import com.example.carsharing.model.payment.Status;
import com.example.carsharing.model.payment.Type;
import com.example.carsharing.model.rental.Rental;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

public class PaymentTestUtil {
    private PaymentTestUtil() {
    }

    public static Payment createDefaultPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Status.PENDING);
        payment.setType(Type.PAYMENT);
        payment.setRental(createRentalForPayment());
        payment.setSessionUrl(createTestUrl());
        payment.setSession("session_123");
        payment.setAmountToPay(new BigDecimal("100.00"));
        return payment;
    }

    public static Payment createSecondPayment() {
        Payment payment = new Payment();
        payment.setId(2L);
        payment.setStatus(Status.PAID);
        payment.setType(Type.FINE);
        payment.setRental(createSecondRental());
        payment.setSessionUrl(createTestUrl());
        payment.setSession("session_456");
        payment.setAmountToPay(new BigDecimal("50.00"));
        return payment;
    }

    public static Payment createPendingPayment() {
        Payment payment = createDefaultPayment();
        payment.setStatus(Status.PENDING);
        return payment;
    }

    public static Rental createRentalForPayment() {
        Rental rental = createDefaultRental();
        rental.setRentalDate(LocalDate.now().minusDays(5));
        rental.setReturnDate(LocalDate.now().minusDays(1));
        rental.setActualReturnDate(LocalDate.now().minusDays(1));
        return rental;
    }

    public static Rental createSecondRental() {
        Rental rental = new Rental();
        rental.setId(2L);
        rental.setRentalDate(LocalDate.now().minusDays(3));
        rental.setReturnDate(LocalDate.now().minusDays(1));
        rental.setActualReturnDate(LocalDate.now());
        rental.setCar(createSecondDefaultCar());
        rental.setUser(createDefaultUser());
        return rental;
    }

    public static Rental createOverdueRental() {
        Rental rental = createDefaultRental();
        rental.setRentalDate(LocalDate.now().minusDays(10));
        rental.setReturnDate(LocalDate.now().minusDays(5)); // Should have returned 5 days ago
        rental.setActualReturnDate(LocalDate.now().minusDays(2)); // Returned 3 days late
        return rental;
    }

    public static PaymentRequestDto createPaymentRequestDto() {
        return new PaymentRequestDto(1L, Type.PAYMENT);
    }

    public static PaymentRequestDto createFineRequestDto() {
        return new PaymentRequestDto(1L, Type.FINE);
    }

    public static PaymentResponseDto createDefaultPaymentResponseDto() {
        return new PaymentResponseDto(
                1L,
                Status.PENDING,
                Type.PAYMENT,
                1L,
                "https://checkout.stripe.com/session_123",
                "session_123",
                new BigDecimal("100.00")
        );
    }

    public static PaymentResponseDto createSecondPaymentResponseDto() {
        return new PaymentResponseDto(
                2L,
                Status.PAID,
                Type.FINE,
                2L,
                "https://checkout.stripe.com/session_456",
                "session_456",
                new BigDecimal("50.00")
        );
    }

    public static PaymentResponseDto createPaidPaymentResponseDto() {
        return new PaymentResponseDto(
                1L,
                Status.PAID,
                Type.PAYMENT,
                1L,
                "https://checkout.stripe.com/session_123",
                "session_123",
                new BigDecimal("100.00")
        );
    }

    private static URL createTestUrl() {
        try {
            return new URL("https://checkout.stripe.com/test");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid test URL", e);
        }
    }
}
