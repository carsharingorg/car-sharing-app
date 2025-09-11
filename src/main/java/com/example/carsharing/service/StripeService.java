package com.example.carsharing.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;

public interface StripeService {
    Session createCheckoutSession(BigDecimal amount, Long rentalId) throws StripeException;
}
