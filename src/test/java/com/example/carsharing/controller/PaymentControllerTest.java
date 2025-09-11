package com.example.carsharing.controller;

import static com.example.carsharing.util.PaymentUtil.createFineRequestDto;
import static com.example.carsharing.util.PaymentUtil.createPaymentRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharing.dto.payment.PaymentRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:db.scripts/add-rentals.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-rentals.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Create payment session with valid data should return PaymentResponseDto")
    void createPaymentSession_WithValidData_ShouldReturnPaymentResponseDto() throws Exception {
        PaymentRequestDto requestDto = createPaymentRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/payments")
                        .with(csrf())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PaymentResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PaymentResponseDto.class
        );

        assertNotNull(actualDto);
        assertNotNull(actualDto.id());
        assertNotNull(actualDto.sessionId());
        assertEquals(requestDto.rentalId(), actualDto.rentalId());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:db.scripts/add-overdue-rentals.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-overdue-rentals.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Create payment session with fine type should return PaymentResponseDto")
    void createPaymentSession_WithFineType_ShouldReturnPaymentResponseDto() throws Exception {
        PaymentRequestDto requestDto = createFineRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/payments")
                        .with(csrf())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PaymentResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PaymentResponseDto.class
        );

        assertNotNull(actualDto);
        assertEquals(requestDto.rentalId(), actualDto.rentalId());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Create payment session with Manager role should return Forbidden")
    void createPaymentSession_WithManagerRole_ShouldReturnForbidden() throws Exception {
        PaymentRequestDto requestDto = createPaymentRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/payments")
                        .with(csrf())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create payment session without authentication should return Unauthorized")
    void createPaymentSession_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        PaymentRequestDto requestDto = createPaymentRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Create payment session with invalid data should return Bad Request")
    void createPaymentSession_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        String invalidJsonRequest = "{}";

        mockMvc.perform(post("/payments")
                        .with(csrf())
                        .content(invalidJsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Create payment session with non-existing rental should return Not Found")
    void createPaymentSession_WithNonExistingRental_ShouldReturnNotFound() throws Exception {
        PaymentRequestDto requestDto = new PaymentRequestDto(999L,
                createPaymentRequestDto().type());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/payments")
                        .with(csrf())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:db.scripts/delete-all-test-data.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/add-payments.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Handle successful payment with valid session ID should return updated payment")
    void handleSuccessfulPayment_WithValidSessionId_ShouldReturnUpdatedPayment() throws Exception {
        String sessionId = "cs_test_111";

        MvcResult result = mockMvc.perform(get("/payments/success/{sessionId}", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PaymentResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PaymentResponseDto.class
        );

        assertNotNull(actualDto);
        assertEquals(sessionId, actualDto.sessionId());
        assertEquals("PAID", actualDto.status().toString());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = "classpath:db.scripts/delete-all-test-data.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/add-payments.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db.scripts/delete-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

    @Test
    @DisplayName("Handle successful payment with Manager role should return updated payment")
    void handleSuccessfulPayment_WithManagerRole_ShouldReturnUpdatedPayment() throws Exception {
        String sessionId = "cs_test_111";

        MvcResult result = mockMvc.perform(get("/payments/success/{sessionId}", sessionId))
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PaymentResponseDto.class
        );

        assertNotNull(actualDto);
        assertEquals(sessionId, actualDto.sessionId());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Handle successful payment with invalid session ID should return Not Found")
    void handleSuccessfulPayment_WithInvalidSessionId_ShouldReturnNotFound() throws Exception {
        String invalidSessionId = "invalid_session";

        mockMvc.perform(get("/payments/success/{sessionId}", invalidSessionId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Handle successful payment with unauthorized role should return Forbidden")
    void handleSuccessfulPayment_WithUnauthorizedRole_ShouldReturnForbidden() throws Exception {
        String sessionId = "session_123";

        mockMvc.perform(get("/payments/success/{sessionId}", sessionId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Handle successful payment without authentication should return Unauthorized")
    void handleSuccessfulPayment_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        String sessionId = "session_123";

        mockMvc.perform(get("/payments/success/{sessionId}", sessionId))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Handle cancelled payment with valid session ID should"
            + " return cancellation message")
    void handleCancelledPayment_WithValidSessionId_ShouldReturnCancellationMessage()
            throws Exception {
        String sessionId = "session_123";
        String expectedMessage = "Payment was cancelled. You can try again later.";

        MvcResult result = mockMvc.perform(get("/payments/cancel/{sessionId}",
                        sessionId))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals(expectedMessage, response);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Handle cancelled payment with Manager role should return cancellation message")
    void handleCancelledPayment_WithManagerRole_ShouldReturnCancellationMessage() throws Exception {
        String sessionId = "session_123";
        String expectedMessage = "Payment was cancelled. You can try again later.";

        MvcResult result = mockMvc.perform(get("/payments/cancel/{sessionId}", sessionId))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals(expectedMessage, response);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Handle cancelled payment with unauthorized role should return Forbidden")
    void handleCancelledPayment_WithUnauthorizedRole_ShouldReturnForbidden() throws Exception {
        String sessionId = "session_123";

        mockMvc.perform(get("/payments/cancel/{sessionId}", sessionId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Handle cancelled payment without authentication should return Unauthorized")
    void handleCancelledPayment_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        String sessionId = "session_123";

        mockMvc.perform(get("/payments/cancel/{sessionId}", sessionId))
                .andExpect(status().isUnauthorized());
    }
}
