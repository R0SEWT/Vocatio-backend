package com.acme.vocatio.controller;

import com.acme.vocatio.dto.profile.ProfileDto;
import com.acme.vocatio.dto.profile.ProfileUpdateRequest;
import com.acme.vocatio.dto.profile.ProfileUpdateResponse;
import com.acme.vocatio.security.UserPrincipal;
import com.acme.vocatio.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints del perfil del usuario autenticado. */
@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /** Recupera el perfil del usuario logueado. */
    @GetMapping
    public ProfileDto getCurrentUserProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return userProfileService.getCurrentUserProfile(principal.getUser().getId());
    }

    /** Actualiza edad, grado e intereses del usuario. */
    @PutMapping
    public ResponseEntity<ProfileUpdateResponse> updateCurrentUserProfile(
            @AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileDto updatedProfile = userProfileService.updateCurrentUserProfile(principal.getUser().getId(), request);
        return ResponseEntity.ok(new ProfileUpdateResponse("Perfil actualizado", updatedProfile));
    }
}
