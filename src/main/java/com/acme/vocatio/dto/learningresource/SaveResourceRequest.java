package com.acme.vocatio.dto.learningresource;

import jakarta.validation.constraints.NotNull;

/**
 * Request para guardar/eliminar un recurso de favoritos.
 */
public record SaveResourceRequest(
    @NotNull(message = "El ID del recurso es obligatorio")
    Long resourceId
) {
}

