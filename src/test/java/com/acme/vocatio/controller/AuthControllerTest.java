package com.acme.vocatio.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserAndReturnTokens() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "nuevo@vocatio.com",
                                  "password": "Password1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registro exitoso"))
                .andExpect(jsonPath("$.user.email").value("nuevo@vocatio.com"))
                .andExpect(jsonPath("$.tokens.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokens.refreshToken").isNotEmpty());
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        String payload = """
                {
                  "email": "repetido@vocatio.com",
                  "password": "Password1"
                }
                """;

        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El email ya está registrado"))
                .andExpect(jsonPath("$.suggestions", hasSize(2)));
    }

    @Test
    void shouldReturnPasswordPolicyErrors() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "policy@vocatio.com",
                                  "password": "abc"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password", hasSize(3)))
                .andExpect(jsonPath(
                                "$.errors.password",
                                containsInAnyOrder(
                                        "La contraseña debe tener al menos 8 caracteres",
                                        "La contraseña debe incluir al menos una letra mayúscula",
                                        "La contraseña debe incluir al menos un número")));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        register("login@vocatio.com");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "login@vocatio.com",
                                  "password": "Password1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inicio de sesión exitoso"))
                .andExpect(jsonPath("$.tokens.accessToken").isNotEmpty());
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        register("invalid@vocatio.com");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "invalid@vocatio.com",
                                  "password": "WrongPass1"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void shouldExtendRefreshTokenWhenRememberMeEnabled() throws Exception {
        register("remember@vocatio.com");

        String rememberResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "remember@vocatio.com",
                                  "password": "Password1",
                                  "rememberMe": true
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode rememberNode = objectMapper.readTree(rememberResponse);
        Instant rememberExpiry = Instant.parse(rememberNode.path("tokens").path("refreshTokenExpiresAt").asText());
        Duration rememberDuration = Duration.between(Instant.now(), rememberExpiry);

        assertThat(rememberDuration).isGreaterThan(Duration.ofDays(20));
    }

    private void register(String email) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "Password1"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated());
    }
}
