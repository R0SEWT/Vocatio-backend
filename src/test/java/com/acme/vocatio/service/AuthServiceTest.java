package com.acme.vocatio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.vocatio.config.JwtProperties;
import com.acme.vocatio.dto.auth.AuthResponse;
import com.acme.vocatio.dto.auth.ChangePasswordRequest;
import com.acme.vocatio.dto.auth.LoginRequest;
import com.acme.vocatio.dto.auth.RegisterRequest;
import com.acme.vocatio.exception.DuplicateEmailException;
import com.acme.vocatio.exception.InvalidCredentialsException;
import com.acme.vocatio.exception.InvalidCurrentPasswordException;
import com.acme.vocatio.exception.InvalidPasswordChangeException;
import com.acme.vocatio.exception.PasswordChangeRateLimitException;
import com.acme.vocatio.exception.UserNotFoundException;
import com.acme.vocatio.model.User;
import com.acme.vocatio.repository.UserRepository;
import com.acme.vocatio.security.JwtService;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordChangeRateLimiter passwordChangeRateLimiter;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");
        testUser.setActive(true);
    }

    // ========== REGISTRO EXITOSO ==========
    @Test
    void givenValidEmailAndPassword_whenRegister_thenCreatesAccountAndReturnsAuthResponse() {
        RegisterRequest request = new RegisterRequest("newuser@example.com", "Password1", false);
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("newuser@example.com");
        savedUser.setPasswordHash("hashedPassword");
        savedUser.setActive(true);

        when(userRepository.existsByEmailIgnoreCase("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(savedUser)).thenReturn("access-token");
        when(jwtService.extractExpiration("access-token")).thenReturn(Instant.now().plusSeconds(900));
        when(jwtProperties.getRefreshTokenTtl()).thenReturn(Duration.ofDays(7));
        when(refreshTokenService.create(eq(savedUser), any(Duration.class)))
                .thenReturn(new RefreshTokenService.RefreshTokenPayload(
                        "refresh-token",
                        Instant.now().plusSeconds(604800)));

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("Registro exitoso");
        assertThat(response.user().id()).isEqualTo(1L);
        assertThat(response.user().email()).isEqualTo("newuser@example.com");
        assertThat(response.tokens().accessToken()).isEqualTo("access-token");
        assertThat(response.tokens().refreshToken()).isEqualTo("refresh-token");
        assertThat(response.tokens().tokenType()).isEqualTo("Bearer");

        verify(userRepository).existsByEmailIgnoreCase("newuser@example.com");
        verify(userRepository).save(any(User.class));
        verify(refreshTokenService).revokeActiveTokens(savedUser);
    }

    @Test
    void givenValidEmailWithRememberMe_whenRegister_thenUsesRememberMeTtl() {
        RegisterRequest request = new RegisterRequest("newuser@example.com", "Password1", true);
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("newuser@example.com");
        savedUser.setPasswordHash("hashedPassword");
        savedUser.setActive(true);

        when(userRepository.existsByEmailIgnoreCase("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(savedUser)).thenReturn("access-token");
        when(jwtService.extractExpiration("access-token")).thenReturn(Instant.now().plusSeconds(900));
        when(jwtProperties.getRememberMeTtl()).thenReturn(Duration.ofDays(30));
        when(refreshTokenService.create(eq(savedUser), any(Duration.class)))
                .thenReturn(new RefreshTokenService.RefreshTokenPayload(
                        "refresh-token",
                        Instant.now().plusSeconds(2592000)));

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        verify(jwtProperties).getRememberMeTtl();
        verify(jwtProperties, never()).getRefreshTokenTtl();
    }

    // ========== EMAIL YA REGISTRADO ==========
    @Test
    void givenExistingEmail_whenRegister_thenThrowsDuplicateEmailException() {
        RegisterRequest request = new RegisterRequest("existing@example.com", "Password1", false);

        when(userRepository.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("El email ya está registrado");

        verify(userRepository).existsByEmailIgnoreCase("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenEmailWithDifferentCase_whenRegister_thenNormalizesAndDetectsDuplicate() {
        RegisterRequest request = new RegisterRequest("User@Example.COM", "Password1", false);

        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("El email ya está registrado");

        verify(userRepository).existsByEmailIgnoreCase("user@example.com");
    }

    // ========== LOGIN EXITOSO ==========
    @Test
    void givenValidCredentials_whenLogin_thenReturnsAuthResponse() {
        LoginRequest request = new LoginRequest("test@example.com", "Password1", false);

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtService.extractExpiration("access-token")).thenReturn(Instant.now().plusSeconds(900));
        when(jwtProperties.getRefreshTokenTtl()).thenReturn(Duration.ofDays(7));
        when(refreshTokenService.create(eq(testUser), any(Duration.class)))
                .thenReturn(new RefreshTokenService.RefreshTokenPayload(
                        "refresh-token",
                        Instant.now().plusSeconds(604800)));

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("Inicio de sesión exitoso");
        assertThat(response.user().id()).isEqualTo(1L);
        assertThat(response.user().email()).isEqualTo("test@example.com");
        assertThat(response.tokens().accessToken()).isEqualTo("access-token");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmailIgnoreCase("test@example.com");
        verify(refreshTokenService).revokeActiveTokens(testUser);
    }

    @Test
    void givenRememberMeTrue_whenLogin_thenUsesRememberMeTtl() {
        LoginRequest request = new LoginRequest("test@example.com", "Password1", true);

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtService.extractExpiration("access-token")).thenReturn(Instant.now().plusSeconds(900));
        when(jwtProperties.getRememberMeTtl()).thenReturn(Duration.ofDays(30));
        when(refreshTokenService.create(eq(testUser), any(Duration.class)))
                .thenReturn(new RefreshTokenService.RefreshTokenPayload(
                        "refresh-token",
                        Instant.now().plusSeconds(2592000)));

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        verify(jwtProperties).getRememberMeTtl();
        verify(jwtProperties, never()).getRefreshTokenTtl();
    }

    // ========== LOGIN FALLIDO ==========
    @Test
    void givenInvalidPassword_whenLogin_thenThrowsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest("test@example.com", "WrongPassword", false);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciales inválidas");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmailIgnoreCase(anyString());
    }

    @Test
    void givenNonExistentEmail_whenLogin_thenThrowsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest("nonexistent@example.com", "Password1", false);

        when(userRepository.findByEmailIgnoreCase("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciales inválidas");
    }

    @Test
    void givenInactiveUser_whenLogin_thenThrowsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest("test@example.com", "Password1", false);
        testUser.setActive(false);

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciales inválidas");

        verify(userRepository).findByEmailIgnoreCase("test@example.com");
    }

    // ========== CAMBIAR CONTRASEÑA - EXITOSO ==========
    @Test
    void givenValidCurrentPasswordAndNewPassword_whenChangePassword_thenUpdatesPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest("Password1", "NewPassword2");

        when(passwordChangeRateLimiter.isBlocked(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password1", testUser.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.matches("NewPassword2", testUser.getPasswordHash())).thenReturn(false);
        when(passwordEncoder.encode("NewPassword2")).thenReturn("newHashedPassword");

        authService.changePassword(1L, request);

        verify(userRepository).save(testUser);
        verify(refreshTokenService).revokeActiveTokens(testUser);
        verify(passwordChangeRateLimiter).reset(1L);
    }

    // ========== CAMBIAR CONTRASEÑA - FALLOS ==========
    @Test
    void givenIncorrectCurrentPassword_whenChangePassword_thenThrowsInvalidCurrentPasswordException() {
        ChangePasswordRequest request = new ChangePasswordRequest("WrongPassword", "NewPassword2");

        when(passwordChangeRateLimiter.isBlocked(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", testUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(1L, request))
                .isInstanceOf(InvalidCurrentPasswordException.class)
                .hasMessage("La contraseña actual no es correcta");

        verify(passwordChangeRateLimiter).recordFailure(1L);
        verify(userRepository, never()).save(any(User.class));
        verify(refreshTokenService, never()).revokeActiveTokens(any(User.class));
    }

    @Test
    void givenSamePasswordAsNew_whenChangePassword_thenThrowsInvalidPasswordChangeException() {
        ChangePasswordRequest request = new ChangePasswordRequest("Password1", "Password1");

        when(passwordChangeRateLimiter.isBlocked(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password1", testUser.getPasswordHash())).thenReturn(true);

        assertThatThrownBy(() -> authService.changePassword(1L, request))
                .isInstanceOf(InvalidPasswordChangeException.class)
                .hasMessage("La nueva contraseña debe ser diferente a la actual");

        verify(passwordChangeRateLimiter).recordFailure(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenNonExistentUser_whenChangePassword_thenThrowsUserNotFoundException() {
        ChangePasswordRequest request = new ChangePasswordRequest("Password1", "NewPassword2");

        when(passwordChangeRateLimiter.isBlocked(999L)).thenReturn(false);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.changePassword(999L, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    // ========== RATE LIMITING ==========
    @Test
    void givenBlockedUser_whenChangePassword_thenThrowsPasswordChangeRateLimitException() {
        ChangePasswordRequest request = new ChangePasswordRequest("Password1", "NewPassword2");

        when(passwordChangeRateLimiter.isBlocked(1L)).thenReturn(true);
        when(passwordChangeRateLimiter.getRemainingLockDuration(1L)).thenReturn(Duration.ofSeconds(60));

        assertThatThrownBy(() -> authService.changePassword(1L, request))
                .isInstanceOf(PasswordChangeRateLimitException.class)
                .hasMessageContaining("Has superado el límite de intentos")
                .hasMessageContaining("60 segundos");

        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any(User.class));
    }
}
