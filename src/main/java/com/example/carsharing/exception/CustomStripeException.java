package com.example.carsharing.exception;

public class CustomStripeException extends RuntimeException {
    public CustomStripeException(String message, Exception e) {
        super(message, e);
    }
}
