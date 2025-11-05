package com.acme.vocatio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.vocatio.dto.account.DeleteAccountRequest;
import com.acme.vocatio.dto.account.DeleteAccountResponse;
import com.acme.vocatio.exception.InvalidAccountDeletionConfirmationException;
import com.acme.vocatio.exception.InvalidCurrentPasswordException;
import com.acme.vocatio.exception.UserNotFoundException;
import com.acme.vocatio.model.User;
import com.acme.vocatio.repository.ProfileRepository;
import com.acme.vocatio.repository.SavedResourceRepository;
import com.acme.vocatio.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SavedResourceRepository savedResourceRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountService userAccountService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");
        testUser.setActive(true);
    }

    // ========== DELETE ACCOUNT - SUCCESS ==========
    @Test
    void givenValidConfirmationAndPassword_whenDeleteAccount_thenDeletesAccountAndAllData() {
        DeleteAccountRequest request = new DeleteAccountRequest("ELIMINAR", "Password1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password1", testUser.getPasswordHash())).thenReturn(true);

        DeleteAccountResponse response = userAccountService.deleteAccount(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.message()).contains("Tu cuenta y datos se eliminaron de forma irreversible");
        assertThat(response.message()).contains("Todas tus sesiones fueron cerradas");
        assertThat(response.pendingDeletion()).isFalse();

        verify(refreshTokenService).deleteAllForUser(testUser);
        verify(savedResourceRepository).deleteAllByUserId(1L);
        verify(profileRepository).deleteById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    void givenConfirmationWithWhitespace_whenDeleteAccount_thenNormalizesAndDeletes() {
        DeleteAccountRequest request = new DeleteAccountRequest("  ELIMINAR  ", "Password1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password1", testUser.getPasswordHash())).thenReturn(true);

        DeleteAccountResponse response = userAccountService.deleteAccount(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.pendingDeletion()).isFalse();

        verify(userRepository).delete(testUser);
    }

    @Test
    void givenLowercaseConfirmation_whenDeleteAccount_thenAcceptsCaseInsensitive() {
        DeleteAccountRequest request = new DeleteAccountRequest("eliminar", "Password1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password1", testUser.getPasswordHash())).thenReturn(true);

        DeleteAccountResponse response = userAccountService.deleteAccount(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.pendingDeletion()).isFalse();

        verify(userRepository).delete(testUser);
    }

    // ========== DELETE ACCOUNT - INVALID CONFIRMATION ==========
    @Test
    void givenIncorrectConfirmation_whenDeleteAccount_thenThrowsInvalidAccountDeletionConfirmationException() {
        DeleteAccountRequest request = new DeleteAccountRequest("DELETE", "Password1");

        assertThatThrownBy(() -> userAccountService.deleteAccount(1L, request))
                .isInstanceOf(InvalidAccountDeletionConfirmationException.class)
                .hasMessage("Debes escribir 'ELIMINAR' para confirmar");

        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void givenEmptyConfirmation_whenDeleteAccount_thenThrowsInvalidAccountDeletionConfirmationException() {
        DeleteAccountRequest request = new DeleteAccountRequest("", "Password1");

        assertThatThrownBy(() -> userAccountService.deleteAccount(1L, request))
                .isInstanceOf(InvalidAccountDeletionConfirmationException.class)
                .hasMessage("Debes escribir 'ELIMINAR' para confirmar");

        verify(userRepository, never()).delete(any());
    }

    @Test
    void givenNullConfirmation_whenDeleteAccount_thenThrowsInvalidAccountDeletionConfirmationException() {
        DeleteAccountRequest request = new DeleteAccountRequest(null, "Password1");

        assertThatThrownBy(() -> userAccountService.deleteAccount(1L, request))
                .isInstanceOf(InvalidAccountDeletionConfirmationException.class)
                .hasMessage("Debes escribir 'ELIMINAR' para confirmar");

        verify(userRepository, never()).delete(any());
    }

    // ========== DELETE ACCOUNT - INVALID PASSWORD ==========
    @Test
    void givenIncorrectPassword_whenDeleteAccount_thenThrowsInvalidCurrentPasswordException() {
        DeleteAccountRequest request = new DeleteAccountRequest("ELIMINAR", "WrongPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", testUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> userAccountService.deleteAccount(1L, request))
                .isInstanceOf(InvalidCurrentPasswordException.class)
                .hasMessage("La contraseña actual no es correcta");

        verify(userRepository, never()).delete(any());
        verify(refreshTokenService, never()).deleteAllForUser(any());
    }

    // ========== DELETE ACCOUNT - NON-EXISTENT USER ==========
    @Test
    void givenNonExistentUser_whenDeleteAccount_thenThrowsUserNotFoundException() {
        DeleteAccountRequest request = new DeleteAccountRequest("ELIMINAR", "Password1");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountService.deleteAccount(999L, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).delete(any());
    }

    // ========== DELETE ACCOUNT - VERIFY CASCADE DELETION ORDER ==========
    @Test
    void whenDeleteAccount_thenDeletesInCorrectOrder() {
        DeleteAccountRequest request = new DeleteAccountRequest("ELIMINAR", "Password1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password1", testUser.getPasswordHash())).thenReturn(true);

        userAccountService.deleteAccount(1L, request);

        // Verify order: tokens -> saved resources -> profile -> user
        var inOrder = org.mockito.Mockito.inOrder(
                refreshTokenService,
                savedResourceRepository,
                profileRepository,
                userRepository);

        inOrder.verify(refreshTokenService).deleteAllForUser(testUser);
        inOrder.verify(savedResourceRepository).deleteAllByUserId(1L);
        inOrder.verify(profileRepository).deleteById(1L);
        inOrder.verify(userRepository).delete(testUser);
    }
}
