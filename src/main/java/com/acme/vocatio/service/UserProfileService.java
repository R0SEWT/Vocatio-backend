package com.acme.vocatio.service;

import com.acme.vocatio.dto.profile.ProfileDto;
import com.acme.vocatio.dto.profile.ProfileUpdateRequest;
import com.acme.vocatio.exception.UserNotFoundException;
import com.acme.vocatio.model.AcademicGrade;
import com.acme.vocatio.model.Profile;
import com.acme.vocatio.model.User;
import com.acme.vocatio.repository.ProfileRepository;
import com.acme.vocatio.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Orquesta la gestión del perfil actual. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;

    /** Devuelve el perfil del usuario autenticado. */
    public ProfileDto getCurrentUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Profile profile = profileRepository.findById(userId).orElse(null);
        return toDto(user, profile);
    }

    /** Guarda los cambios de edad, grado e intereses. */
    @Transactional
    public ProfileDto updateCurrentUserProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Profile profile = profileRepository
                .findById(userId)
                .orElseGet(() -> {
                    Profile newProfile = new Profile();
                    newProfile.setId(userId);
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setUser(user);
        user.setProfile(profile);
        profile.setAge(request.age().shortValue());

        AcademicGrade grade = AcademicGrade
                .fromCode(request.grade())
                .orElseThrow(() -> new IllegalStateException("Grado académico no reconocido"));
        profile.setGrade(grade);

        List<String> normalizedInterests = normalizeInterests(request.interests());
        profile.setPersonalInterests(writeInterests(normalizedInterests));

        Profile saved = profileRepository.save(profile);
        return toDto(user, saved, normalizedInterests);
    }

    /** Limpia espacios y evita duplicados en intereses (insensible a mayúsculas). */
    private List<String> normalizeInterests(List<String> rawInterests) {
        Map<String, String> normalized = new LinkedHashMap<>();
        for (String interest : rawInterests) {
            if (interest == null) {
                continue;
            }

            String trimmed = interest.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String key = trimmed.toLowerCase(Locale.ROOT);
            normalized.putIfAbsent(key, trimmed);
        }
        return List.copyOf(normalized.values());
    }

    /** Serializa los intereses a JSON. */
    private String writeInterests(List<String> interests) {
        try {
            return objectMapper.writeValueAsString(interests);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("No fue posible guardar los intereses del perfil", ex);
        }
    }

    /** Deserializa los intereses almacenados. */
    private List<String> readInterests(String interestsJson) {
        if (interestsJson == null || interestsJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(interestsJson, STRING_LIST_TYPE);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("No fue posible leer los intereses almacenados", ex);
        }
    }

    /** Crea el DTO usando los datos existentes. */
    private ProfileDto toDto(User user, Profile profile) {
        List<String> interests = profile == null ? List.of() : readInterests(profile.getPersonalInterests());
        return toDto(user, profile, interests);
    }

    /** Ajusta el DTO con edad, grado e intereses. */
    private ProfileDto toDto(User user, Profile profile, List<String> interests) {
        Integer age = null;
        String gradeCode = null;
        String gradeLabel = null;

        if (profile != null) {
            if (profile.getAge() != null) {
                age = profile.getAge().intValue();
            }
            if (profile.getGrade() != null) {
                gradeCode = profile.getGrade().getCode();
                gradeLabel = profile.getGrade().getDisplayName();
            }
        }

        List<String> safeInterests = interests == null ? List.of() : List.copyOf(interests);

        return new ProfileDto(user.getId(), user.getEmail(), age, gradeCode, gradeLabel, safeInterests);
    }
}
