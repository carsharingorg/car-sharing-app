package com.example.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharing.dto.rental.RentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WithUserDetails("customer@example.com")
    @Sql(scripts = {"classpath:db.scripts/add-rental-test-users.sql",
            "classpath:db.scripts/add-rental-test-cars.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db.scripts/delete-rental-test-cars.sql",
            "classpath:db.scripts/delete-rental-test-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Create rental with valid data should return RentalResponseDto")
    void createRental_WithValidData_ShouldReturnRentalResponseDto() throws Exception {
        RentalRequestDto requestDto = new RentalRequestDto(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                1L
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/rentals")
                        .with(csrf())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        RentalResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseDto.class
        );

        assertNotNull(actualDto);
        assertNotNull(actualDto.id());
        assertEquals(requestDto.rentalDate(), actualDto.rentalDate());
        assertEquals(requestDto.returnDate(), actualDto.returnDate());
        assertEquals(requestDto.carId(), actualDto.carId());
        assertNull(actualDto.actualReturnDate());
    }

    @WithUserDetails("manager@example.com")
    @Sql(scripts = {"classpath:db.scripts/delete-rental-test-cars.sql",
            "classpath:db.scripts/delete-rental-test-users.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db.scripts/add-rental-test-users.sql",
            "classpath:db.scripts/add-rental-test-cars.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:db.scripts/delete-rental-test-cars.sql",
            "classpath:db.scripts/delete-rental-test-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Create rental with Manager role should return RentalResponseDto")
    void createRental_WithManagerRole_ShouldReturnRentalResponseDto() throws Exception {
        RentalRequestDto requestDto = new RentalRequestDto(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                1L
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/rentals")
                        .with(csrf())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:db.scripts/add-single-rental.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-single-rental.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Get rental by valid ID should return RentalResponseDto")
    void getRental_WithValidId_ShouldReturnRentalResponseDto() throws Exception {
        Long rentalId = 1L;

        MvcResult result = mockMvc.perform(get("/rentals/{id}", rentalId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        RentalResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseDto.class
        );

        assertNotNull(actualDto);
        assertEquals(rentalId, actualDto.id());
        assertNotNull(actualDto.rentalDate());
        assertNotNull(actualDto.returnDate());
        assertNotNull(actualDto.carId());
        assertNotNull(actualDto.userId());
    }

    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    @Sql(scripts = "classpath:db.scripts/delete-single-rental.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/add-single-rental.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-single-rental.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Get rental by ID with Manager role should return RentalResponseDto")
    void getRental_WithManagerRole_ShouldReturnRentalResponseDto() throws Exception {
        Long rentalId = 1L;

        mockMvc.perform(get("/rentals/{id}", rentalId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get rental with non-existing ID should return Not Found")
    void getRental_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        Long invalidRentalId = 999L;

        mockMvc.perform(get("/rentals/{id}", invalidRentalId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get rental without authentication should return Unauthorized")
    void getRental_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        Long rentalId = 1L;

        mockMvc.perform(get("/rentals/{id}", rentalId))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:db.scripts/add-mixed-rentals.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-mixed-rentals.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Get active rentals should return only active rentals")
    void getRentals_WithActiveFilter_ShouldReturnOnlyActiveRentals() throws Exception {
        Long userId = 1L;
        boolean isActive = true;

        MvcResult result = mockMvc.perform(get("/rentals")
                        .param("user_id", userId.toString())
                        .param("is_active", String.valueOf(isActive)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        RentalResponseDto[] rentals = objectMapper.readValue(jsonResponse,
                RentalResponseDto[].class);
        List<RentalResponseDto> rentalList = Arrays.asList(rentals);

        assertNotNull(rentalList);
        assertEquals(2, rentalList.size());

        rentalList.forEach(rental -> {
            assertNull(rental.actualReturnDate());
            assertEquals(userId, rental.userId());
        });
    }

    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:db.scripts/add-mixed-rentals.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-mixed-rentals.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Get completed rentals should return only completed rentals")
    void getRentals_WithCompletedFilter_ShouldReturnOnlyCompletedRentals() throws Exception {
        Long userId = 1L;
        boolean isActive = false;

        MvcResult result = mockMvc.perform(get("/rentals")
                        .param("user_id", userId.toString())
                        .param("is_active", String.valueOf(isActive)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        RentalResponseDto[] rentals = objectMapper.readValue(jsonResponse,
                RentalResponseDto[].class);
        List<RentalResponseDto> rentalList = Arrays.asList(rentals);

        assertNotNull(rentalList);
        assertEquals(2, rentalList.size());

        rentalList.forEach(rental -> {
            assertNotNull(rental.actualReturnDate());
            assertEquals(userId, rental.userId());
        });
    }

    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get rentals without userId parameter should return Bad Request")
    void getRentals_WithoutUserIdParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/rentals")
                        .param("is_active", "true"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get rentals without isActive parameter should return Bad Request")
    void getRentals_WithoutIsActiveParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/rentals")
                        .param("user_id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get rentals without authentication should return Unauthorized")
    void getRentals_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/rentals")
                        .param("user_id", "1")
                        .param("is_active", "true"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("customer@example.com")
    @Sql(scripts = "classpath:db.scripts/add-rental-for-return.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-rental-for-return.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Return active rental should return updated RentalResponseDto")
    void returnRental_WithActiveRental_ShouldReturnUpdatedRental() throws Exception {
        Long rentalId = 1L;

        MvcResult result = mockMvc.perform(post("/rentals/{id}/return", rentalId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        RentalResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseDto.class
        );

        assertNotNull(actualDto);
        assertEquals(rentalId, actualDto.id());
        assertNotNull(actualDto.actualReturnDate());
        assertEquals(LocalDate.now(), actualDto.actualReturnDate());
    }

    @Test
    @DisplayName("Return rental without authentication should return Unauthorized")
    void returnRental_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        Long rentalId = 1L;

        mockMvc.perform(post("/rentals/{id}/return", rentalId))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("customer@example.com")
    @Sql(scripts = "classpath:db.scripts/delete-overdue-rental-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/add-overdue-rental-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-overdue-rental-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Return overdue rental should return updated RentalResponseDto")
    void returnRental_WithOverdueRental_ShouldReturnUpdatedRental() throws Exception {
        Long rentalId = 1L;

        MvcResult result = mockMvc.perform(post("/rentals/{id}/return", rentalId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        RentalResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseDto.class
        );

        assertNotNull(actualDto);
        assertEquals(rentalId, actualDto.id());
        assertNotNull(actualDto.actualReturnDate());
        assertEquals(LocalDate.now(), actualDto.actualReturnDate());
    }
}
