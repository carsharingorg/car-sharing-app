package com.example.carsharing.controller;

import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.security.AuthenticationService;
import com.example.carsharing.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental API", description = "Operations related to car rentals")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;
    private final AuthenticationService authenticationService;

    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new rental",
            description = "Add a new rental for the current user")
    @PostMapping
    public RentalResponseDto addRental(@RequestBody RentalRequestDto requestDto) {
        return rentalService.addRental(authenticationService.getCurrentUserId(), requestDto);
    }

    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Get rental by id",
            description = "Retrieve rental details by rental id")
    @GetMapping("/{id}")
    public RentalResponseDto getRental(@PathVariable Long id) {
        return rentalService.get(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Get rentals by user id and active status",
            description = "Retrieve a list of rentals filtered by user"
                    + " id and whether they are active")
    public List<RentalResponseDto> findAllByUserIdAndIsActive(
            @RequestParam("user_id") Long userId,
            @RequestParam("is_active") boolean isActive) {
        return rentalService.findAllByUserIdAndIsActive(userId, isActive);
    }

    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Return a rental",
            description = "Mark a rental as returned by rental id for the current user")
    @PostMapping("/{id}/return")
    public RentalResponseDto returnRental(@PathVariable Long id) {
        return rentalService.returnRental(id, authenticationService.getCurrentUserId());
    }
}
