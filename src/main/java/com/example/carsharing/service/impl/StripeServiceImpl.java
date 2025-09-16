package com.example.carsharing.service.impl;

import com.example.carsharing.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Transactional
public class StripeServiceImpl implements StripeService {
    private static final String PRODUCT_DATA_NAME = "Car Rental Payment";
    private static final String PRODUCT_DATA_DESCRIPTION = "Payment for car rental service";
    private static final String DEFAULT_CURRENCY = "usd";
    private static final String CONVERT_TO_CENTS = "100";
    private static final long DEFAULT_QUANTITY_OF_CARS = 1L;
    private static final String METADATA_NAME = "rentalId";
    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public Session createCheckoutSession(BigDecimal amount, Long rentalId) throws StripeException {
        String successUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/payments/success/{sessionId}")
                .buildAndExpand("{CHECKOUT_SESSION_ID}")
                .toUriString();

        String cancelUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/payments/cancel/{sessionId}")
                .buildAndExpand("{CHECKOUT_SESSION_ID}")
                .toUriString();

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(PRODUCT_DATA_NAME)
                .setDescription(PRODUCT_DATA_DESCRIPTION)
                .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(DEFAULT_CURRENCY)
                .setProductData(productData)
                .setUnitAmount(amount.multiply(new BigDecimal(CONVERT_TO_CENTS)).longValue())
                .build();

        SessionCreateParams.LineItem lineItemData = SessionCreateParams.LineItem.builder()
                .setPriceData(priceData)
                .setQuantity(DEFAULT_QUANTITY_OF_CARS)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(lineItemData)
                .putMetadata(METADATA_NAME, rentalId.toString())
                .build();

        RequestOptions options = RequestOptions.builder()
                .setConnectTimeout(30 * 1000)
                .setReadTimeout(24 * 60 * 60 * 1000)
                .build();
        return Session.create(params, options);
    }
}
