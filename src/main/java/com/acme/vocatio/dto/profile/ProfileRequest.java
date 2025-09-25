package com.acme.vocatio.dto.profile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record ProfileRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String name,
        
        @NotNull(message = "La edad es obligatoria")
        @Min(value = 12, message = "La edad mínima es 12 años")
        @Max(value = 120, message = "La edad máxima es 120 años")
        Short age,
        
        @NotBlank(message = "El grado es obligatorio")
        @Size(max = 50, message = "El grado no puede tener más de 50 caracteres")
        String grade,
        
        Map<String, Double> personalInterests
) {}