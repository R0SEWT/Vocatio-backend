package com.acme.vocatio.dto.learningresource;

import java.util.List;

/**
 * Respuesta paginada de recursos de aprendizaje.
 */
public record LearningResourceResponse(
    List<LearningResourceDto> resources,
    int currentPage,
    int totalPages,
    long totalElements,
    String message
) {
}

