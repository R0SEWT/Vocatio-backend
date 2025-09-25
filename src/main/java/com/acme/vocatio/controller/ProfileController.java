package com.acme.vocatio.controller;

import com.acme.vocatio.dto.profile.InterestStatistics;
import com.acme.vocatio.dto.profile.ProfileRequest;
import com.acme.vocatio.dto.profile.ProfileResponse;
import com.acme.vocatio.security.UserPrincipal;
import com.acme.vocatio.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Obtener el perfil del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        ProfileResponse profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Obtener el perfil de un usuario específico (por ID)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable Long userId) {
        ProfileResponse profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Actualizar el perfil del usuario autenticado
     */
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody ProfileRequest request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        ProfileResponse updatedProfile = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Obtener estadísticas de intereses del usuario autenticado
     */
    @GetMapping("/interests/statistics")
    public ResponseEntity<InterestStatistics> getInterestStatistics(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        InterestStatistics statistics = profileService.getInterestStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Obtener estadísticas de intereses de un usuario específico
     */
    @GetMapping("/{userId}/interests/statistics")
    public ResponseEntity<InterestStatistics> getInterestStatisticsById(@PathVariable Long userId) {
        InterestStatistics statistics = profileService.getInterestStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Endpoint para verificar el estado del perfil
     */
    @GetMapping("/status")
    public ResponseEntity<ProfileStatusResponse> getProfileStatus(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        try {
            ProfileResponse profile = profileService.getProfileByUserId(userId);
            boolean isComplete = isProfileComplete(profile);
            
            return ResponseEntity.ok(new ProfileStatusResponse(
                    true,
                    isComplete,
                    isComplete ? "Perfil completo" : "Perfil incompleto",
                    profile.personalInterests().areas().size()
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new ProfileStatusResponse(
                    false,
                    false,
                    "Perfil no encontrado",
                    0
            ));
        }
    }

    private boolean isProfileComplete(ProfileResponse profile) {
        return profile.name() != null && !profile.name().trim().isEmpty() &&
               profile.age() != null &&
               profile.grade() != null && !profile.grade().trim().isEmpty() &&
               profile.personalInterests() != null &&
               !profile.personalInterests().areas().isEmpty();
    }

    /**
     * Record para respuesta de estado del perfil
     */
    public record ProfileStatusResponse(
            boolean exists,
            boolean isComplete,
            String message,
            int interestsCount
    ) {}
}