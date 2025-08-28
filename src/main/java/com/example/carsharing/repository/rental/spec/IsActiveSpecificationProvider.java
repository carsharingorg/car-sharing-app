package com.example.carsharing.repository.rental.spec;

import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.repository.SpecificationProvider;
import com.example.carsharing.repository.rental.RentalSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;

public class IsActiveSpecificationProvider implements SpecificationProvider<Rental> {
    private static final String FIELD_ACTUAL_RETURN_DATE = "actualReturnDate";

    @Override
    public String getKey() {
        return RentalSpecificationBuilder.IS_ACTIVE_KEY;
    }

    @Override
    public Specification<Rental> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            if (params.length == 0) {
                return criteriaBuilder.conjunction();
            }

            boolean isActive = Boolean.parseBoolean(params[0]);

            if (isActive) {
                return criteriaBuilder.isNull(root.get(FIELD_ACTUAL_RETURN_DATE));
            } else {
                return criteriaBuilder.isNotNull(root.get(FIELD_ACTUAL_RETURN_DATE));
            }
        };
    }
}
