package com.acme.vocatio.service;

import com.acme.vocatio.model.Profile;
import com.acme.vocatio.repository.ProfileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;

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
                .orElseThrow(() -> new IllegalArgumentException("Perfil con id " + id + " no encontrado."));
    }

    @Transactional
    public Profile update(Long id, Profile profile) {
        if (!profileRepository.existsById(id)) {
            throw new IllegalArgumentException("Perfil con id " + id + " no existe.");
        }
        profile.setId(id);
        return profileRepository.save(profile);
    }

    @Transactional
    public void delete(Long id) {
        if (!profileRepository.existsById(id)) {
            throw new IllegalArgumentException("Perfil con id " + id + " no existe.");
        }
        profileRepository.deleteById(id);
    }

    public List<Profile> findByProfileName(String name) {
        return profileRepository.findByNameContainingIgnoreCase(name);
    }
}
