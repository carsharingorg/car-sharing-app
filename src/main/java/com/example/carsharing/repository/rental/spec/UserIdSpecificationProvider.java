package com.example.carsharing.repository.rental.spec;

import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.repository.SpecificationProvider;
import com.example.carsharing.repository.rental.RentalSpecificationBuilder;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;

public class UserIdSpecificationProvider implements SpecificationProvider<Rental> {
    private static final String FIELD_USER = "user";
    private static final String FIELD_ID = "id";

    @Override
    public String getKey() {
        return RentalSpecificationBuilder.USER_ID_KEY;
    }

    @Override
    public Specification<Rental> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            Long[] userIds = Arrays.stream(params)
                    .map(Long::valueOf)
                    .toArray(Long[]::new);
            return root.get(FIELD_USER).get(FIELD_ID).in(Arrays.asList(userIds));
        };
    }
}
