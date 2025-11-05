package com.acme.vocatio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acme.vocatio.dto.auth.AuthResponse;
import com.acme.vocatio.dto.auth.ChangePasswordRequest;
import com.acme.vocatio.dto.auth.LoginRequest;
import com.acme.vocatio.dto.auth.RegisterRequest;
import com.acme.vocatio.exception.DuplicateEmailException;
import com.acme.vocatio.exception.InvalidCredentialsException;
import com.acme.vocatio.exception.InvalidCurrentPasswordException;
import com.acme.vocatio.exception.InvalidPasswordChangeException;
import com.acme.vocatio.exception.PasswordChangeRateLimitException;
import com.acme.vocatio.model.User;
import com.acme.vocatio.security.UserPrincipal;
import com.acme.vocatio.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private User testUser;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");

        AuthResponse.UserSummary userSummary = new AuthResponse.UserSummary(1L, "test@example.com");
        AuthResponse.TokenBundle tokens = new AuthResponse.TokenBundle(
                "Bearer",
                "access-token",
                Instant.now().plusSeconds(900),
                "refresh-token",
                Instant.now().plusSeconds(604800));
        authResponse = new AuthResponse("Registro exitoso", userSummary, tokens);
    }

    // ========== REGISTER - SUCCESS ==========
    @Test
    void givenValidRegisterRequest_whenRegister_thenReturns201AndAuthResponse() throws Exception {
        RegisterRequest request = new RegisterRequest("newuser@example.com", "Password1", false);

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registro exitoso"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.tokens.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.tokens.accessToken").value("access-token"))
                .andExpect(jsonPath("$.tokens.refreshToken").value("refresh-token"));

        verify(authService).register(any(RegisterRequest.class));
    }

    // ========== REGISTER - DUPLICATE EMAIL ==========
    @Test
    void givenExistingEmail_whenRegister_thenReturns409() throws Exception {
        RegisterRequest request = new RegisterRequest("existing@example.com", "Password1", false);

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateEmailException("El email ya está registrado"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El email ya está registrado"));
    }

    // ========== REGISTER - VALIDATION ERRORS ==========
    @Test
    void givenInvalidEmail_whenRegister_thenReturns400WithValidationErrors() throws Exception {
        String invalidRequest = """
                {
                  "email": "invalid-email",
                  "password": "Password1"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenShortPassword_whenRegister_thenReturns400WithValidationErrors() throws Exception {
        String invalidRequest = """
                {
                  "email": "test@example.com",
                  "password": "Pass1"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenPasswordWithoutUppercase_whenRegister_thenReturns400() throws Exception {
        String invalidRequest = """
                {
                  "email": "test@example.com",
                  "password": "password1"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenPasswordWithoutDigit_whenRegister_thenReturns400() throws Exception {
        String invalidRequest = """
                {
                  "email": "test@example.com",
                  "password": "Password"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    // ========== LOGIN - SUCCESS ==========
    @Test
    void givenValidLoginRequest_whenLogin_thenReturns200AndAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "Password1", false);
        AuthResponse loginResponse = new AuthResponse(
                "Inicio de sesión exitoso",
                authResponse.user(),
                authResponse.tokens());

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inicio de sesión exitoso"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.tokens.accessToken").value("access-token"));

        verify(authService).login(any(LoginRequest.class));
    }

    // ========== LOGIN - INVALID CREDENTIALS ==========
    @Test
    void givenInvalidCredentials_whenLogin_thenReturns401() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "WrongPassword", false);

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    // ========== LOGIN - VALIDATION ERRORS ==========
    @Test
    void givenInvalidEmailFormat_whenLogin_thenReturns400() throws Exception {
        String invalidRequest = """
                {
                  "email": "not-an-email",
                  "password": "Password1"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenMissingPassword_whenLogin_thenReturns400() throws Exception {
        String invalidRequest = """
                {
                  "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    // ========== CHANGE PASSWORD - SUCCESS ==========
    @Test
    @WithMockUser
    void givenValidPasswordChange_whenChangePassword_thenReturns200() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("OldPassword1", "NewPassword2");
        UserPrincipal principal = new UserPrincipal(testUser);

        mockMvc.perform(post("/auth/change-password")
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contraseña actualizada. Inicia sesión nuevamente."))
                .andExpect(jsonPath("$.requiresReAuth").value(true));

        verify(authService).changePassword(eq(1L), any(ChangePasswordRequest.class));
    }

    // ========== CHANGE PASSWORD - INVALID CURRENT PASSWORD ==========
    @Test
    @WithMockUser
    void givenInvalidCurrentPassword_whenChangePassword_thenReturns401() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("WrongPassword", "NewPassword2");
        UserPrincipal principal = new UserPrincipal(testUser);

        doThrow(new InvalidCurrentPasswordException("La contraseña actual no es correcta"))
                .when(authService).changePassword(eq(1L), any(ChangePasswordRequest.class));

        mockMvc.perform(post("/auth/change-password")
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("La contraseña actual no es correcta"));
    }

    // ========== CHANGE PASSWORD - SAME PASSWORD ==========
    @Test
    @WithMockUser
    void givenSamePassword_whenChangePassword_thenReturns400() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("Password1", "Password1");
        UserPrincipal principal = new UserPrincipal(testUser);

        doThrow(new InvalidPasswordChangeException("La nueva contraseña debe ser diferente a la actual"))
                .when(authService).changePassword(eq(1L), any(ChangePasswordRequest.class));

        mockMvc.perform(post("/auth/change-password")
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La nueva contraseña debe ser diferente a la actual"));
    }

    // ========== CHANGE PASSWORD - RATE LIMITED ==========
    @Test
    @WithMockUser
    void givenRateLimitExceeded_whenChangePassword_thenReturns429() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("Password1", "NewPassword2");
        UserPrincipal principal = new UserPrincipal(testUser);

        doThrow(new PasswordChangeRateLimitException(
                        "Has superado el límite de intentos. Intenta nuevamente en 60 segundos.",
                        60))
                .when(authService).changePassword(eq(1L), any(ChangePasswordRequest.class));

        mockMvc.perform(post("/auth/change-password")
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("Has superado el límite de intentos. Intenta nuevamente en 60 segundos."));
    }

    // ========== CHANGE PASSWORD - VALIDATION ERRORS ==========
    @Test
    @WithMockUser
    void givenInvalidNewPassword_whenChangePassword_thenReturns400() throws Exception {
        String invalidRequest = """
                {
                  "currentPassword": "Password1",
                  "newPassword": "weak"
                }
                """;

        UserPrincipal principal = new UserPrincipal(testUser);

        mockMvc.perform(post("/auth/change-password")
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void givenMissingCurrentPassword_whenChangePassword_thenReturns400() throws Exception {
        String invalidRequest = """
                {
                  "newPassword": "NewPassword2"
                }
                """;

        UserPrincipal principal = new UserPrincipal(testUser);

        mockMvc.perform(post("/auth/change-password")
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }
}
