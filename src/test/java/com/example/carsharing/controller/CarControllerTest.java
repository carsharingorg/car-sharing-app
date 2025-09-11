package com.example.carsharing.controller;

import static com.example.carsharing.util.CarUtil.createDefaultCarRequestDto;
import static com.example.carsharing.util.CarUtil.createDefaultCarResponseDto;
import static com.example.carsharing.util.CarUtil.createNonValidCarRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharing.dto.car.CarRequestDto;
import com.example.carsharing.dto.car.CarResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CarControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void createCar_WithValidRequest_ShouldReturnValidCarDto() throws Exception {
        CarRequestDto requestDto = createDefaultCarRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CarResponseDto expectedDto = createDefaultCarResponseDto();

        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CarResponseDto actualDto = objectMapper.readValue(result.getResponse()
                        .getContentAsString(),
                CarResponseDto.class);
        assertEquals(expectedDto, actualDto);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void createCar_NonValidRequest_ShouldReturnValidCarDto() throws Exception {
        CarRequestDto requestDto = createNonValidCarRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Create car with Customer role should return Forbidden")
    void createCar_WithCustomerRole_ShouldReturnForbidden() throws Exception {
        CarRequestDto request = createDefaultCarRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void getCarById_NonExistingId_ShouldReturnNotFound() throws Exception {
        long nonExistingId = 999L;
        mockMvc.perform(get("/cars/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:/db.scripts/create-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:/db.scripts/delete-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCarById_ExistingId_ShouldReturnCarResponseDto() throws Exception {
        long existingId = 1L;
        CarResponseDto expectedDto = createDefaultCarResponseDto();

        MvcResult result = mockMvc.perform(get("/cars/{id}", existingId))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actualDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CarResponseDto.class);
        assertEquals(expectedDto, actualDto);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = "classpath:/db.scripts/create-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:/db.scripts/delete-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void deleteCarById_ValidId_ShouldReturnNoContent() throws Exception {
        long existingId = 1L;
        mockMvc.perform(delete("/cars/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = "classpath:/db.scripts/create-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:/db.scripts/delete-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void deleteCarById_NonExistingValidId_ShouldReturnBadRequest() throws Exception {
        long existingId = 999L;
        mockMvc.perform(delete("/cars/{id}", existingId))
                .andExpect(status().isNotFound());
    }

    @Sql(scripts = "classpath:/db.scripts/delete-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:/db.scripts/create-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:/db.scripts/delete-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Get all cars should return paginated list")
    void getAll_WithValidPagination_ShouldReturnPagedCars() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode pageNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = pageNode.get("content");

        List<CarResponseDto> cars = objectMapper.convertValue(
                contentNode,
                new TypeReference<List<CarResponseDto>>() {}
        );

        assertEquals(2, cars.size());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = "classpath:/db.scripts/create-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:/db.scripts/delete-two-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Update car with invalid data should return bad request")
    void updateCarById_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        long existingId = 1L;
        CarRequestDto invalidRequest = createNonValidCarRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(put("/cars/{id}", existingId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
