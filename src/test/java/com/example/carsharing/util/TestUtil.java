package com.example.carsharing.util;

import com.example.carsharing.dto.car.CarRequestDto;
import com.example.carsharing.dto.car.CarResponseDto;
import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.dto.user.UserProfileDto;
import com.example.carsharing.dto.user.UserProfilePatchDto;
import com.example.carsharing.dto.user.UserRegisterDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.model.car.Car;
import com.example.carsharing.model.car.Type;
import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.model.user.Role;
import com.example.carsharing.model.user.RoleName;
import com.example.carsharing.model.user.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class TestUtil {
    private TestUtil() {
    }

    public static Car createDefaultCar() {
        Car car = new Car();
        car.setId(1L);
        car.setModel("M3");
        car.setBrand("BMW");
        car.setType(Type.SEDAN);
        car.setInventory(100);
        car.setDailyFee(new BigDecimal(100));
        return car;
    }

    public static Car createSecondDefaultCar() {
        Car car = new Car();
        car.setId(2L);
        car.setModel("A6");
        car.setBrand("Audi");
        car.setType(Type.SUV);
        car.setInventory(200);
        car.setDailyFee(new BigDecimal(200));
        return car;
    }

    public static CarResponseDto createDefaultCarResponseDto() {
        return new CarResponseDto(
                1L,
                "M3",
                "BMW",
                Type.SEDAN,
                new BigDecimal(100)
        );
    }

    public static CarResponseDto createSecondDefaultCarResponseDto() {
        return new CarResponseDto(
                2L,
                "A6",
                "Audi",
                Type.SUV,
                new BigDecimal(200)
        );
    }

    public static CarRequestDto createDefaultCarRequestDto() {
        return new CarRequestDto(
                "M3",
                "BMW",
                Type.SEDAN,
                100,
                new BigDecimal(100)
        );
    }

    public static CarRequestDto createSecondDefaultCarRequestDto() {
        return new CarRequestDto(
                "A6",
                "Audi",
                Type.SUV,
                200,
                new BigDecimal(200)
        );
    }

    public static Car createUpdatedDefaultCar() {
        Car car = new Car();
        car.setId(1L);
        car.setModel("Updated_M3");
        car.setBrand("BMW");
        car.setType(Type.SEDAN);
        car.setInventory(100);
        car.setDailyFee(new BigDecimal(100));
        return car;
    }

    public static CarRequestDto createUpdatedDefaultCarRequestDto() {
        return new CarRequestDto(
                "Updated_M3",
                "BMW",
                Type.SEDAN,
                100,
                new BigDecimal(100)
        );
    }

    public static CarResponseDto createUpdatedDefaultCarResponseDto() {
        return new CarResponseDto(
                1L,
                "Updated_M3",
                "BMW",
                Type.SEDAN,
                new BigDecimal(100)
        );
    }

    public static User createDefaultUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setRoles(Set.of(createDefaultRole()));
        user.setDeleted(false);
        return user;
    }

    public static Role createDefaultRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_CUSTOMER);
        return role;
    }

    public static Role createManagerRole() {
        Role role = new Role();
        role.setId(2L);
        role.setName(RoleName.ROLE_MANAGER);
        return role;
    }

    public static UserRegisterDto createDefaultUserRegisterDto() {
        return new UserRegisterDto(
                "test@example.com",
                "password123",
                "password123",
                "John",
                "Doe"
        );
    }

    public static UserResponseDto createDefaultUserResponseDto() {
        return new UserResponseDto(
                1L,
                "test@example.com",
                "John",
                "Doe"
        );
    }

    public static UserProfileDto createDefaultUserProfileDto() {
        return new UserProfileDto(
                "updated@example.com",
                "Jane",
                "Smith"
        );
    }

    public static UserProfilePatchDto createDefaultUserProfilePatchDto() {
        return new UserProfilePatchDto(
                "patched@example.com",
                "Updated John",
                "Updated Doe"
        );
    }

    public static Rental createDefaultRental() {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.now().plusDays(1));
        rental.setReturnDate(LocalDate.now().plusDays(5));
        rental.setActualReturnDate(null);
        rental.setCar(createDefaultCar());
        rental.setUser(createDefaultUser());
        return rental;
    }

    public static Rental createActiveRental() {
        Rental rental = createDefaultRental();
        rental.setActualReturnDate(null);
        return rental;
    }

    public static Rental createClosedRental() {
        Rental rental = createDefaultRental();
        rental.setActualReturnDate(LocalDate.now());
        return rental;
    }

    public static RentalRequestDto createDefaultRentalRequestDto() {
        return new RentalRequestDto(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                1L
        );
    }

    public static RentalRequestDto createPastDateRentalRequestDto() {
        return new RentalRequestDto(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5),
                1L
        );
    }

    public static RentalRequestDto createInvalidReturnDateRentalRequestDto() {
        return new RentalRequestDto(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(3),
                1L
        );
    }

    public static RentalResponseDto createDefaultRentalResponseDto() {
        return new RentalResponseDto(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                null,
                1L,
                1L
        );

    }
}
