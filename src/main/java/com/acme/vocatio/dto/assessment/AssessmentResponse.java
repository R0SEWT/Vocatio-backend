package com.acme.vocatio.dto.assessment;

import java.time.LocalDateTime;

public record AssessmentResponse(
        String assessmentId,
        String status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        Questions questions
) {

    public record Questions(
            String testType,
            Integer totalQuestions,
            Integer currentQuestion,
            QuestionData currentQuestionData
    ) {}

    public record QuestionData(
            Long questionId,
            String questionText,
            java.util.List<Option> options
    ) {}

    public record Option(
            Long optionId,
            String optionText,
            String areaInteres
    ) {}
}