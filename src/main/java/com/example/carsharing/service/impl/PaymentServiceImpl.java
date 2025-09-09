package com.example.carsharing.service.impl;

import com.example.carsharing.dto.payment.PaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import com.example.carsharing.exception.CustomStripeException;
import com.example.carsharing.exception.EntityNotFoundException;
import com.example.carsharing.mapper.PaymentMapper;
import com.example.carsharing.model.payment.Payment;
import com.example.carsharing.model.payment.Status;
import com.example.carsharing.model.payment.Type;
import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.repository.payment.PaymentRepository;
import com.example.carsharing.repository.rental.RentalRepository;
import com.example.carsharing.service.PaymentService;
import com.example.carsharing.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    @Value("${app.fine-multiplier}")
    private BigDecimal fineMultiplier;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;

    @Override
    public PaymentResponseDto createPaymentSession(PaymentRequestDto requestDto) {
        Rental rental = rentalRepository.findById(requestDto.rentalId()).orElseThrow(
                () -> new EntityNotFoundException("Rental not found with id: "
                        + requestDto.rentalId()));

        Optional<Payment> existingPayment = paymentRepository
                .findByRentalIdAndTypeAndStatus(rental.getId(), requestDto.type(), Status.PENDING);

        BigDecimal amountToPay = calculatePaymentAmount(rental, requestDto.type());
        try {
            Session session = stripeService.createCheckoutSession(amountToPay,
                    requestDto.rentalId());

            Payment payment = existingPayment.orElse(new Payment());
            payment.setStatus(Status.PENDING);
            payment.setType(requestDto.type());
            payment.setRental(rental);
            payment.setSessionUrl(new URL(session.getUrl()));
            payment.setSession(session.getId());
            payment.setAmountToPay(amountToPay);

            Payment savedPayment = paymentRepository.save(payment);
            return paymentMapper.toDto(savedPayment);
        } catch (StripeException e) {
            throw new CustomStripeException("Failed to create Stripe session: "
                    + e.getMessage(), e) {
            };
        } catch (MalformedURLException e) {
            throw new CustomStripeException("Invalid session URL from Stripe", e);
        }
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByUserId(Long userId) {
        List<Payment> paymentList = paymentRepository.findAllByUserId(userId);
        return paymentList.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponseDto handleSuccessfulPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found "
                        + "with session ID: " + sessionId));

        payment.setStatus(Status.PAID);
        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public String handleCancelledPayment(String sessionId) {
        return "Payment was cancelled. You can try again later.";
    }

    private BigDecimal calculatePaymentAmount(Rental rental, Type type) {
        if (type == Type.PAYMENT) {
            LocalDate plannedEnd = rental.getReturnDate();
            LocalDate actualEnd = rental.getActualReturnDate();

            LocalDate effectiveEnd = (actualEnd != null && actualEnd.isBefore(plannedEnd))
                    ? actualEnd
                    : plannedEnd;

            long rentalDays = ChronoUnit.DAYS.between(rental.getRentalDate(),
                    effectiveEnd);

            rentalDays = Math.max(rentalDays, 1);
            return rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(rentalDays));
        }
        if (type == Type.FINE) {
            if (rental.getActualReturnDate() == null || !rental.getActualReturnDate()
                    .isAfter(rental.getReturnDate())) {
                throw new IllegalArgumentException("No overdue days found "
                        + "for fine calculation");
            }
            long overdueDays = ChronoUnit.DAYS.between(rental.getReturnDate(),
                    rental.getActualReturnDate());
            return rental.getCar().getDailyFee()
                    .multiply(BigDecimal.valueOf(overdueDays))
                    .multiply(fineMultiplier);
        }
        throw new IllegalArgumentException("Invalid payment type: " + type);
    }
}
