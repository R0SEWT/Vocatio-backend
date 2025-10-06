package com.acme.vocatio.dto.learningresource;

/**
 * DTO para mostrar información de un recurso de aprendizaje.
 */
public record LearningResourceDto(
    Long id,
    Long careerId,
    String titulo,
    String urlRecurso,
    String descripcion,
    Integer duracionMinutos,
    Long areaInteresId,
    boolean isSaved
) {
}

