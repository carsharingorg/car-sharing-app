package com.example.carsharing.service.impl;

import static com.example.carsharing.util.PaymentTestUtil.createDefaultPayment;
import static com.example.carsharing.util.PaymentTestUtil.createDefaultPaymentResponseDto;
import static com.example.carsharing.util.PaymentTestUtil.createFineRequestDto;
import static com.example.carsharing.util.PaymentTestUtil.createOverdueRental;
import static com.example.carsharing.util.PaymentTestUtil.createPaidPaymentResponseDto;
import static com.example.carsharing.util.PaymentTestUtil.createPaymentRequestDto;
import static com.example.carsharing.util.PaymentTestUtil.createPendingPayment;
import static com.example.carsharing.util.PaymentTestUtil.createRentalForPayment;
import static com.example.carsharing.util.PaymentTestUtil.createSecondPayment;
import static com.example.carsharing.util.PaymentTestUtil.createSecondPaymentResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharing.dto.payment.PaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.mapper.PaymentMapper;
import com.example.carsharing.model.payment.Payment;
import com.example.carsharing.model.payment.Status;
import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.repository.payment.PaymentRepository;
import com.example.carsharing.repository.rental.RentalRepository;
import com.example.carsharing.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripeService stripeService;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void createPaymentSession_NewPaymentSession_ShouldReturnPaymentResponseDto()
            throws StripeException, MalformedURLException {
        ReflectionTestUtils.setField(paymentService, "fineMultiplier",
                new BigDecimal("1.5"));

        PaymentRequestDto requestDto = createPaymentRequestDto();
        Rental rental = createRentalForPayment();
        Session mockSession = createMockStripeSession();
        Payment payment = createDefaultPayment();
        PaymentResponseDto expectedDto = createDefaultPaymentResponseDto();

        when(rentalRepository.findById(requestDto.rentalId())).thenReturn(Optional.of(rental));
        when(paymentRepository.findByRentalIdAndTypeAndStatus(rental.getId(),
                requestDto.type(), Status.PENDING))
                .thenReturn(Optional.empty());
        when(stripeService.createCheckoutSession(any(BigDecimal.class),
                eq(requestDto.rentalId())))
                .thenReturn(mockSession);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expectedDto);

        PaymentResponseDto actualDto = paymentService.createPaymentSession(requestDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(rentalRepository).findById(requestDto.rentalId());
        verify(paymentRepository).findByRentalIdAndTypeAndStatus(rental.getId(),
                requestDto.type(), Status.PENDING);
        verify(stripeService).createCheckoutSession(any(BigDecimal.class),
                eq(requestDto.rentalId()));
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void createPaymentSession_ExistingPendingPayment_ShouldUpdateExistingPayment()
            throws StripeException, MalformedURLException {
        ReflectionTestUtils.setField(paymentService, "fineMultiplier",
                new BigDecimal("1.5"));

        PaymentRequestDto requestDto = createPaymentRequestDto();
        Rental rental = createRentalForPayment();
        Payment existingPayment = createDefaultPayment();
        Session mockSession = createMockStripeSession();
        PaymentResponseDto expectedDto = createDefaultPaymentResponseDto();

        when(rentalRepository.findById(requestDto.rentalId())).thenReturn(Optional.of(rental));
        when(paymentRepository.findByRentalIdAndTypeAndStatus(rental.getId(),
                requestDto.type(), Status.PENDING))
                .thenReturn(Optional.of(existingPayment));
        when(stripeService.createCheckoutSession(any(BigDecimal.class),
                eq(requestDto.rentalId())))
                .thenReturn(mockSession);
        when(paymentRepository.save(existingPayment)).thenReturn(existingPayment);
        when(paymentMapper.toDto(existingPayment)).thenReturn(expectedDto);

        PaymentResponseDto actualDto = paymentService.createPaymentSession(requestDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(paymentRepository).save(existingPayment);
    }

    @Test
    void createPaymentSession_RentalNotFound_ShouldThrowEntityNotFoundException()
            throws StripeException {
        PaymentRequestDto requestDto = createPaymentRequestDto();

        when(rentalRepository.findById(requestDto.rentalId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.createPaymentSession(requestDto)
        );

        assertEquals("Rental not found with id: 1", exception.getMessage());
        verify(rentalRepository).findById(requestDto.rentalId());
        verify(stripeService, never()).createCheckoutSession(any(), any());
    }

    @Test
    void createPaymentSession_FineCalculation_ShouldCalculateCorrectAmount()
            throws StripeException, MalformedURLException {
        ReflectionTestUtils.setField(paymentService, "fineMultiplier",
                new BigDecimal("2.0"));

        PaymentRequestDto fineRequestDto = createFineRequestDto();
        Rental overdueRental = createOverdueRental();
        Session mockSession = createMockStripeSession();
        Payment payment = createDefaultPayment();
        PaymentResponseDto expectedDto = createDefaultPaymentResponseDto();

        when(rentalRepository.findById(fineRequestDto.rentalId()))
                .thenReturn(Optional.of(overdueRental));
        when(paymentRepository.findByRentalIdAndTypeAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(stripeService.createCheckoutSession(any(BigDecimal.class),
                eq(fineRequestDto.rentalId())))
                .thenReturn(mockSession);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expectedDto);

        PaymentResponseDto actualDto = paymentService.createPaymentSession(fineRequestDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(stripeService).createCheckoutSession(any(BigDecimal.class),
                eq(fineRequestDto.rentalId()));
    }

    @Test
    void createPaymentSession_FineWithoutOverdue_ShouldThrowIllegalArgumentException()
            throws StripeException {
        ReflectionTestUtils.setField(paymentService, "fineMultiplier",
                new BigDecimal("1.5"));

        PaymentRequestDto fineRequestDto = createFineRequestDto();
        Rental rental = createRentalForPayment(); // No overdue

        when(rentalRepository.findById(fineRequestDto.rentalId()))
                .thenReturn(Optional.of(rental));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPaymentSession(fineRequestDto)
        );

        assertEquals("No overdue days found for fine calculation",
                exception.getMessage());
        verify(stripeService, never()).createCheckoutSession(any(), any());
    }

    @Test
    void getPaymentsByUserId_ShouldReturnListOfPayments() {
        Long userId = 1L;
        List<Payment> payments = List.of(createDefaultPayment(), createSecondPayment());
        List<PaymentResponseDto> expectedDtos = List.of(
                createDefaultPaymentResponseDto(),
                createSecondPaymentResponseDto()
        );

        when(paymentRepository.findAllByUserId(userId)).thenReturn(payments);
        when(paymentMapper.toDto(payments.get(0))).thenReturn(expectedDtos.get(0));
        when(paymentMapper.toDto(payments.get(1))).thenReturn(expectedDtos.get(1));

        List<PaymentResponseDto> actualDtos = paymentService.getPaymentsByUserId(userId);

        assertNotNull(actualDtos);
        assertEquals(2, actualDtos.size());
        assertEquals(expectedDtos, actualDtos);
        verify(paymentRepository).findAllByUserId(userId);
        verify(paymentMapper, times(2)).toDto(any(Payment.class));
    }

    @Test
    void getPaymentsByUserId_EmptyList_ShouldReturnEmptyList() {
        Long userId = 1L;

        when(paymentRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<PaymentResponseDto> actualDtos = paymentService.getPaymentsByUserId(userId);

        assertNotNull(actualDtos);
        assertTrue(actualDtos.isEmpty());
        verify(paymentRepository).findAllByUserId(userId);
        verify(paymentMapper, never()).toDto(any());
    }

    @Test
    void handleSuccessfulPayment_ExistingPayment_ShouldUpdateStatusToPaid() {
        String sessionId = "session_123";
        Payment payment = createPendingPayment();
        PaymentResponseDto expectedDto = createPaidPaymentResponseDto();

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expectedDto);

        PaymentResponseDto actualDto = paymentService.handleSuccessfulPayment(sessionId);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        assertEquals(Status.PAID, payment.getStatus());
        verify(paymentRepository).findBySessionId(sessionId);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void handleSuccessfulPayment_PaymentNotFound_ShouldThrowEntityNotFoundException() {
        String sessionId = "nonexistent_session";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.handleSuccessfulPayment(sessionId)
        );

        assertEquals("Payment not found with session ID: nonexistent_session",
                exception.getMessage());
        verify(paymentRepository).findBySessionId(sessionId);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void handleCancelledPayment_ShouldReturnCancelMessage() {
        String sessionId = "cancelled_session";

        String result = paymentService.handleCancelledPayment(sessionId);

        assertEquals("Payment was cancelled. You can try again later.", result);
    }

    private Session createMockStripeSession() throws MalformedURLException {
        Session session = mock(Session.class);
        when(session.getUrl()).thenReturn("https://checkout.stripe.com/session_123");
        when(session.getId()).thenReturn("session_123");
        return session;
    }
}
