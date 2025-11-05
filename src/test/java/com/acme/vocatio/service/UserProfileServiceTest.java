package com.acme.vocatio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.vocatio.dto.profile.PersonalDataUpdateRequest;
import com.acme.vocatio.dto.profile.ProfileDto;
import com.acme.vocatio.dto.profile.ProfileUpdateRequest;
import com.acme.vocatio.exception.InvalidPersonalDataException;
import com.acme.vocatio.exception.UserNotFoundException;
import com.acme.vocatio.model.AcademicGrade;
import com.acme.vocatio.model.Profile;
import com.acme.vocatio.model.User;
import com.acme.vocatio.repository.ProfileRepository;
import com.acme.vocatio.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private UserProfileService userProfileService;

    private User testUser;
    private Profile testProfile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testProfile = new Profile();
        testProfile.setUser(testUser);
        testProfile.setAge((short) 18);
        testProfile.setGrade(AcademicGrade.SECUNDARIA_4);
        testProfile.setPersonalInterests("[\"Tecnología\",\"Arte\"]");
        testProfile.setName("John Doe");
        testProfile.setPublicPreferences("{\"newsletter\":true,\"publicProfile\":false}");
    }

    // ========== GET CURRENT USER PROFILE ==========
    @Test
    void givenExistingProfile_whenGetCurrentUserProfile_thenReturnsProfileDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));

        ProfileDto result = userProfileService.getCurrentUserProfile(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.name()).isEqualTo("John Doe");
        assertThat(result.age()).isEqualTo(18);
        assertThat(result.grade()).isEqualTo("SECUNDARIA_4");
        assertThat(result.gradeLabel()).isEqualTo("4° de secundaria");
        assertThat(result.interests()).containsExactly("Tecnología", "Arte");
        assertThat(result.preferences()).containsEntry("newsletter", true);
        assertThat(result.preferences()).containsEntry("publicProfile", false);

        verify(userRepository).findById(1L);
        verify(profileRepository).findById(1L);
    }

    @Test
    void givenNoProfile_whenGetCurrentUserProfile_thenReturnsEmptyProfileDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        ProfileDto result = userProfileService.getCurrentUserProfile(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.name()).isNull();
        assertThat(result.age()).isNull();
        assertThat(result.grade()).isNull();
        assertThat(result.gradeLabel()).isNull();
        assertThat(result.interests()).isEmpty();
        assertThat(result.preferences()).isEmpty();
    }

    @Test
    void givenNonExistentUser_whenGetCurrentUserProfile_thenThrowsUserNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getCurrentUserProfile(999L))
                .isInstanceOf(UserNotFoundException.class);

        verify(profileRepository, never()).findById(any());
    }

    // ========== UPDATE CURRENT USER PROFILE (Age, Grade, Interests) ==========
    @Test
    void givenValidProfileData_whenUpdateCurrentUserProfile_thenSavesAndReturnsUpdatedProfile() {
        ProfileUpdateRequest request = new ProfileUpdateRequest(20, "UNIVERSIDAD_2", List.of("Ciencia", "Música"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDto result = userProfileService.updateCurrentUserProfile(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.age()).isEqualTo(20);
        assertThat(result.grade()).isEqualTo("UNIVERSIDAD_2");
        assertThat(result.gradeLabel()).isEqualTo("2° ciclo universitario");
        assertThat(result.interests()).containsExactly("Ciencia", "Música");

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();
        assertThat(savedProfile.getAge()).isEqualTo((short) 20);
        assertThat(savedProfile.getGrade()).isEqualTo(AcademicGrade.UNIVERSIDAD_2);
        assertThat(savedProfile.getPersonalInterests()).contains("Ciencia");
        assertThat(savedProfile.getPersonalInterests()).contains("Música");
    }

    @Test
    void givenNoExistingProfile_whenUpdateCurrentUserProfile_thenCreatesNewProfile() {
        ProfileUpdateRequest request = new ProfileUpdateRequest(18, "SECUNDARIA_4", List.of("Deportes"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDto result = userProfileService.updateCurrentUserProfile(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.age()).isEqualTo(18);
        assertThat(result.grade()).isEqualTo("SECUNDARIA_4");
        assertThat(result.interests()).containsExactly("Deportes");

        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void givenDuplicateInterests_whenUpdateCurrentUserProfile_thenNormalizesAndDeduplicates() {
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                18,
                "SECUNDARIA_4",
                List.of("  Tecnología  ", "Arte", "TECNOLOGÍA", "arte", "Tecnología"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDto result = userProfileService.updateCurrentUserProfile(1L, request);

        assertThat(result.interests()).containsExactly("Tecnología", "Arte");
    }

    @Test
    void givenInvalidGradeCode_whenUpdateCurrentUserProfile_thenThrowsIllegalStateException() {
        ProfileUpdateRequest request = new ProfileUpdateRequest(18, "INVALID_GRADE", List.of("Tecnología"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));

        assertThatThrownBy(() -> userProfileService.updateCurrentUserProfile(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Grado académico no reconocido");

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void givenNonExistentUser_whenUpdateCurrentUserProfile_thenThrowsUserNotFoundException() {
        ProfileUpdateRequest request = new ProfileUpdateRequest(18, "SECUNDARIA_4", List.of("Tecnología"));

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.updateCurrentUserProfile(999L, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(profileRepository, never()).save(any(Profile.class));
    }

    // ========== UPDATE PERSONAL DATA (Name and Preferences) ==========
    @Test
    void givenValidPersonalData_whenUpdatePersonalData_thenSavesAndReturnsUpdatedProfile() {
        PersonalDataUpdateRequest request = new PersonalDataUpdateRequest(
                "Jane Smith",
                Map.of("newsletter", false, "publicProfile", true));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDto result = userProfileService.updatePersonalData(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Jane Smith");
        assertThat(result.preferences()).containsEntry("newsletter", false);
        assertThat(result.preferences()).containsEntry("publicProfile", true);

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();
        assertThat(savedProfile.getName()).isEqualTo("Jane Smith");
        assertThat(savedProfile.getPublicPreferences()).contains("newsletter");
        assertThat(savedProfile.getPublicPreferences()).contains("publicProfile");
    }

    @Test
    void givenNoExistingProfile_whenUpdatePersonalData_thenCreatesNewProfile() {
        PersonalDataUpdateRequest request = new PersonalDataUpdateRequest(
                "John Doe",
                Map.of("newsletter", true));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDto result = userProfileService.updatePersonalData(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("John Doe");
        assertThat(result.preferences()).containsEntry("newsletter", true);

        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void givenNameWithWhitespace_whenUpdatePersonalData_thenTrimsName() {
        PersonalDataUpdateRequest request = new PersonalDataUpdateRequest(
                "  Jane Smith  ",
                Map.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDto result = userProfileService.updatePersonalData(1L, request);

        assertThat(result.name()).isEqualTo("Jane Smith");

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        assertThat(profileCaptor.getValue().getName()).isEqualTo("Jane Smith");
    }

    @Test
    void givenNullPreferenceValue_whenUpdatePersonalData_thenThrowsInvalidPersonalDataException() {
        Map<String, Boolean> preferencesWithNull = new java.util.HashMap<>();
        preferencesWithNull.put("newsletter", null);
        PersonalDataUpdateRequest request = new PersonalDataUpdateRequest(
                "John Doe",
                preferencesWithNull);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));

        assertThatThrownBy(() -> userProfileService.updatePersonalData(1L, request))
                .isInstanceOf(InvalidPersonalDataException.class)
                .hasMessageContaining("El valor de la preferencia 'newsletter' es obligatorio");

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void givenEmptyPreferenceKey_whenUpdatePersonalData_thenIgnoresIt() {
        PersonalDataUpdateRequest request = new PersonalDataUpdateRequest(
                "John Doe",
                Map.of("  ", true, "newsletter", false));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDto result = userProfileService.updatePersonalData(1L, request);

        assertThat(result.preferences()).containsKey("newsletter");
        assertThat(result.preferences()).doesNotContainKey("  ");
    }

    @Test
    void givenNonExistentUser_whenUpdatePersonalData_thenThrowsUserNotFoundException() {
        PersonalDataUpdateRequest request = new PersonalDataUpdateRequest("John Doe", Map.of());

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.updatePersonalData(999L, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(profileRepository, never()).save(any(Profile.class));
    }

    // ========== EDGE CASES ==========
    @Test
    void givenNullInterestsInProfile_whenGetCurrentUserProfile_thenReturnsEmptyList() {
        testProfile.setPersonalInterests(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));

        ProfileDto result = userProfileService.getCurrentUserProfile(1L);

        assertThat(result.interests()).isEmpty();
    }

    @Test
    void givenBlankInterestsInProfile_whenGetCurrentUserProfile_thenReturnsEmptyList() {
        testProfile.setPersonalInterests("   ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));

        ProfileDto result = userProfileService.getCurrentUserProfile(1L);

        assertThat(result.interests()).isEmpty();
    }

    @Test
    void givenNullPreferencesInProfile_whenGetCurrentUserProfile_thenReturnsEmptyMap() {
        testProfile.setPublicPreferences(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));

        ProfileDto result = userProfileService.getCurrentUserProfile(1L);

        assertThat(result.preferences()).isEmpty();
    }
}
