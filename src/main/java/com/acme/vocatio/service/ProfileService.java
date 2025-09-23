package com.acme.vocatio.service;

import com.acme.vocatio.model.Profile;
import com.acme.vocatio.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository ProfileRepository;


    public Profile create(Profile Profile) {
        return ProfileRepository.save(Profile);
    }


    public List<Profile> findAll() {
        return ProfileRepository.findAll();
    }


    public Profile findById(Long id) {
        return ProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil con id " + id + " no encontrado."));
    }


    public Profile update(Long id, Profile Profile) {
        if (!ProfileRepository.existsById(id)) {
            throw new RuntimeException("Libro con id " + id + " no existe.");
        }
        Profile.setId(id);
        return ProfileRepository.save(Profile);
    }


    public void delete(Long id) {
        if (!ProfileRepository.existsById(id)) {
            throw new RuntimeException("Libro con id " + id + " no existe.");
        }
        ProfileRepository.deleteById(id);
    }


    public List<Profile> findByProfileName(String user_id) {
        return ProfileRepository.findByProfileId(user_id);
    }
}