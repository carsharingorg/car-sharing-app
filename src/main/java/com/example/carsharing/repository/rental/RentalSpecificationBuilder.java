package com.example.carsharing.repository.rental;

import com.example.carsharing.dto.rental.RentalSearchParametersDto;
import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.repository.SpecificationBuilder;
import com.example.carsharing.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    public static final String USER_ID_KEY = "user_id";
    public static final String IS_ACTIVE_KEY = "isActive";
    private final SpecificationProviderManager<Rental> rentalSpecificationProviderManager;

    @Override
    public Specification<Rental> build(RentalSearchParametersDto searchParameters) {
        Specification<Rental> spec = Specification.allOf();
        if (searchParameters.userId() != null && searchParameters.userId().length > 0) {
            spec = spec.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(USER_ID_KEY)
                    .getSpecification(searchParameters.userId()));
        }
        if (searchParameters.isActive() != null && searchParameters.isActive().length > 0) {
            spec = spec.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(IS_ACTIVE_KEY)
                    .getSpecification(searchParameters.isActive()));
        }
        return spec;
    }
}
