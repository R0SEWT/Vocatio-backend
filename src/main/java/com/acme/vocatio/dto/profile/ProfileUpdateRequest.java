package com.acme.vocatio.dto.profile;

import com.acme.vocatio.validation.ValidAcademicGrade;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Campos permitidos para actualizar el perfil. */
public record ProfileUpdateRequest(
        @NotNull(message = "La edad es obligatoria")
                @Min(value = 13, message = "La edad debe estar entre 13 y 30 años")
                @Max(value = 30, message = "La edad debe estar entre 13 y 30 años")
                Integer age,

        @NotBlank(message = "El grado académico es obligatorio")
                @ValidAcademicGrade
                String grade,

        @NotEmpty(message = "Selecciona al menos un interés")
                List<@NotBlank(message = "El interés no puede estar vacío") String> interests) {}
