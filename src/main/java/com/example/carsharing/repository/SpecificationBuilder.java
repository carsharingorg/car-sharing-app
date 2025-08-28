package com.example.carsharing.repository;

import com.example.carsharing.dto.rental.RentalSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(RentalSearchParametersDto rentalSearchParametersDto);
}
