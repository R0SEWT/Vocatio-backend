package com.acme.vocatio.dto.profile;

import java.util.List;

/** Vista consolidada del perfil personal. */
public record ProfileDto(
        Long id,
        String email,
        Integer age,
        String grade,
        String gradeLabel,
        List<String> interests) {}
