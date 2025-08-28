package com.example.carsharing.controller;

import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.dto.rental.RentalSearchParametersDto;
import com.example.carsharing.security.AuthenticationService;
import com.example.carsharing.service.RentalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public RentalResponseDto addRental(@RequestBody RentalRequestDto requestDto) {
        return rentalService.addRental(authenticationService.getCurrentUserId(), requestDto);
    }

    @GetMapping("/{id}")
    public RentalResponseDto getRental(@PathVariable Long id) {
        return rentalService.get(id);
    }

    @GetMapping()
    public List<RentalResponseDto> search(RentalSearchParametersDto searchParameters) {
        return rentalService.search(searchParameters);
    }

    @PostMapping("/{id}/return")
    public RentalResponseDto returnRental(@PathVariable Long id) {
        return rentalService.returnRental(id, authenticationService.getCurrentUserId());
    }
}
