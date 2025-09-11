package com.example.carsharing.mapper;

import com.example.carsharing.config.MapperConfig;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import com.example.carsharing.model.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "rental.id", target = "rentalId")
    @Mapping(source = "session", target = "sessionId")
    PaymentResponseDto toDto(Payment savedPayment);

    default String mapUrlToString(java.net.URL url) {
        return url != null ? url.toString() : null;
    }
}
