package com.acme.vocatio.service;

import com.acme.vocatio.dto.profile.InterestStatistics;
import com.acme.vocatio.dto.profile.ProfileRequest;
import com.acme.vocatio.dto.profile.ProfileResponse;
import com.acme.vocatio.exception.ProfileNotFoundException;
import com.acme.vocatio.model.Profile;
import com.acme.vocatio.repository.ProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Profile create(Profile profile) {
        return profileRepository.save(profile);
    }

    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    public Profile findById(Long id) {
        return profileRepository
                .findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));
    }

    public ProfileResponse getProfileByUserId(Long userId) {
        Profile profile = profileRepository
                .findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
        
        return mapToProfileResponse(profile);
    }

    @Transactional
    public Profile update(Long id, Profile profile) {
        if (!profileRepository.existsById(id)) {
            throw new ProfileNotFoundException(id);
        }
        profile.setId(id);
        return profileRepository.save(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileRequest request) {
        Profile profile = profileRepository
                .findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        // Actualizar campos básicos
        profile.setName(request.name());
        profile.setAge(request.age());
        profile.setGrade(request.grade());

        // Actualizar intereses personales (convertir Map a JSON string)
        if (request.personalInterests() != null) {
            try {
                profile.setPersonalInterests(objectMapper.writeValueAsString(request.personalInterests()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error al procesar intereses personales", e);
            }
        }

        Profile updatedProfile = profileRepository.save(profile);
        return mapToProfileResponse(updatedProfile);
    }

    @Transactional
    public void delete(Long id) {
        if (!profileRepository.existsById(id)) {
            throw new ProfileNotFoundException(id);
        }
        profileRepository.deleteById(id);
    }

    public List<Profile> findByProfileName(String name) {
        return profileRepository.findByNameContainingIgnoreCase(name);
    }

    public InterestStatistics getInterestStatistics(Long userId) {
        Profile profile = profileRepository
                .findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        Map<String, Double> interests = parsePersonalInterests(profile.getPersonalInterests());
        
        if (interests.isEmpty()) {
            return new InterestStatistics(
                new HashMap<>(), 
                "Sin datos", 
                0.0, 
                0, 
                "Beginner"
            );
        }

        // Calcular estadísticas
        String dominantArea = interests.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin datos");

        Double averageScore = interests.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        String strengthLevel = calculateStrengthLevel(averageScore);

        return new InterestStatistics(
                interests,
                dominantArea,
                averageScore,
                1, // Por ahora asumimos 1 test, esto debería calcularse desde la base de datos
                strengthLevel
        );
    }

    private ProfileResponse mapToProfileResponse(Profile profile) {
        Map<String, Double> interests = parsePersonalInterests(profile.getPersonalInterests());
        
        // Calcular área dominante y promedio
        String dominantArea = interests.isEmpty() ? "Sin datos" : 
                interests.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("Sin datos");

        Double averageScore = interests.isEmpty() ? 0.0 :
                interests.values().stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);

        ProfileResponse.InterestProfile interestProfile = new ProfileResponse.InterestProfile(
                interests,
                dominantArea,
                averageScore
        );

        return new ProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getAge(),
                profile.getGrade(),
                profile.getUser() != null ? profile.getUser().getEmail() : null,
                interestProfile
        );
    }

    private Map<String, Double> parsePersonalInterests(String personalInterestsJson) {
        if (personalInterestsJson == null || personalInterestsJson.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(personalInterestsJson, new TypeReference<Map<String, Double>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }

    private String calculateStrengthLevel(Double averageScore) {
        if (averageScore >= 8.0) return "Expert";
        if (averageScore >= 6.0) return "Advanced";
        if (averageScore >= 4.0) return "Intermediate";
        if (averageScore >= 2.0) return "Beginner";
        return "Starter";
    }
}
