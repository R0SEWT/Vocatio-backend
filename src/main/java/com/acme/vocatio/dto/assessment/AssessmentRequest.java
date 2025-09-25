package com.acme.vocatio.dto.assessment;

import jakarta.validation.constraints.NotNull;

public record AssessmentRequest(
        @NotNull(message = "El tipo de test es requerido")
        String testType,
        
        Long questionId,
        Long selectedOptionId
) {}