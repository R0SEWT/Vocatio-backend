package com.acme.vocatio.dto.profile;

import java.util.Map;

public record InterestStatistics(
        Map<String, Double> areaScores,
        String dominantArea,
        Double averageScore,
        Integer totalTests,
        String strengthLevel
) {}