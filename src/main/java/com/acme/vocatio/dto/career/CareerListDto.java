package com.acme.vocatio.dto.career;

/**
 * DTO para representar la información básica de una carrera al listar.
 * Incluye campos opcionales para filtros futuros.
 */
public record CareerListDto(
        Long id,
        String nombre,
        String descripcion,
        String perfilRequerido,

        String modalidad //opcional
) {}
