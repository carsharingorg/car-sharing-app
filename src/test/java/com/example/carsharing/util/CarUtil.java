package com.example.carsharing.util;

import com.example.carsharing.dto.car.CarRequestDto;
import com.example.carsharing.dto.car.CarResponseDto;
import com.example.carsharing.model.car.Car;
import com.example.carsharing.model.car.Type;
import java.math.BigDecimal;

public class CarUtil {
    private CarUtil() {
    }

    public static Car createDefaultCar() {
        Car car = new Car();
        car.setId(1L);
        car.setModel("M3");
        car.setBrand("BMW");
        car.setType(Type.SEDAN);
        car.setInventory(100);
        car.setDailyFee(new BigDecimal("100.56"));
        return car;
    }

    public static Car createSecondDefaultCar() {
        Car car = new Car();
        car.setId(2L);
        car.setModel("A6");
        car.setBrand("Audi");
        car.setType(Type.SUV);
        car.setInventory(200);
        car.setDailyFee(new BigDecimal("55.66"));
        return car;
    }

    public static CarResponseDto createDefaultCarResponseDto() {
        return new CarResponseDto(
                1L,
                "M3",
                "BMW",
                Type.SEDAN,
                new BigDecimal("100.56")
        );
    }

    public static CarResponseDto createSecondDefaultCarResponseDto() {
        return new CarResponseDto(
                2L,
                "A6",
                "Audi",
                Type.SUV,
                new BigDecimal("55.66")
        );
    }

    public static CarRequestDto createDefaultCarRequestDto() {
        return new CarRequestDto(
                "M3",
                "BMW",
                Type.SEDAN,
                100,
                new BigDecimal("100.56")
        );
    }

    public static CarRequestDto createSecondDefaultCarRequestDto() {
        return new CarRequestDto(
                "A6",
                "Audi",
                Type.SUV,
                200,
                new BigDecimal("55.66")
        );
    }

    public static Car createUpdatedDefaultCar() {
        Car car = new Car();
        car.setId(1L);
        car.setModel("Updated_M3");
        car.setBrand("BMW");
        car.setType(Type.SEDAN);
        car.setInventory(100);
        car.setDailyFee(new BigDecimal("100.56"));
        return car;
    }

    public static CarRequestDto createUpdatedDefaultCarRequestDto() {
        return new CarRequestDto(
                "Updated_M3",
                "BMW",
                Type.SEDAN,
                100,
                new BigDecimal("100.56")
        );
    }

    public static CarResponseDto createUpdatedDefaultCarResponseDto() {
        return new CarResponseDto(
                1L,
                "Updated_M3",
                "BMW",
                Type.SEDAN,
                new BigDecimal("100.56")
        );
    }

    public static CarRequestDto createNonValidCarRequestDto() {
        return new CarRequestDto(
                "Updated_M3",
                "BMW",
                Type.SEDAN,
                -100,
                new BigDecimal("100.56")
        );
    }
}
