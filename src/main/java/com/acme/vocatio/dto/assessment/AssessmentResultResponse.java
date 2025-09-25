package com.acme.vocatio.dto.assessment;

import java.time.LocalDateTime;
import java.util.Map;

public record AssessmentResultResponse(
        String assessmentId,
        String status,
        LocalDateTime completedAt,
        Results results,
        Recommendations recommendations
) {

    public record Results(
            Map<String, Double> areaScores,
            String dominantArea,
            Double averageScore,
            String strengthLevel
    ) {}

    public record Recommendations(
            java.util.List<CareerRecommendation> careers,
            java.util.List<ResourceRecommendation> resources,
            String nextSteps
    ) {}

    public record CareerRecommendation(
            Long careerId,
            String careerName,
            String description,
            Double matchPercentage,
            String reasoning
    ) {}

    public record ResourceRecommendation(
            Long resourceId,
            String title,
            String type,
            String url,
            String description
    ) {}
}