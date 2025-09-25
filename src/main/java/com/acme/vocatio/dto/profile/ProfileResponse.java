package com.acme.vocatio.dto.profile;

import java.util.Map;

public record ProfileResponse(
        Long id,
        String name,
        Short age,
        String grade,
        String email,
        InterestProfile personalInterests
) {

    public record InterestProfile(
            Map<String, Double> areas,
            String dominantArea,
            Double averageScore
    ) {}
}