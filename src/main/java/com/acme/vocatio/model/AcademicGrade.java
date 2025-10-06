package com.acme.vocatio.model;

import java.util.Arrays;
import java.util.Optional;

/** Catálogo alineado con la estructura educativa peruana. */
public enum AcademicGrade {

    SECUNDARIA_1("1° de secundaria"),
    SECUNDARIA_2("2° de secundaria"),
    SECUNDARIA_3("3° de secundaria"),
    SECUNDARIA_4("4° de secundaria"),
    SECUNDARIA_5("5° de secundaria"),
    SUPERIOR_TECNICA_1("1° ciclo de instituto técnico"),
    SUPERIOR_TECNICA_2("2° ciclo de instituto técnico"),
    SUPERIOR_TECNICA_3("3° ciclo de instituto técnico"),
    SUPERIOR_TECNICA_4("4° ciclo de instituto técnico"),
    SUPERIOR_TECNICA_5_MAS("5° ciclo o más de instituto técnico"),
    UNIVERSIDAD_1("1° ciclo universitario"),
    UNIVERSIDAD_2("2° ciclo universitario"),
    UNIVERSIDAD_3("3° ciclo universitario"),
    UNIVERSIDAD_4("4° ciclo universitario"),
    UNIVERSIDAD_5("5° ciclo universitario"),
    UNIVERSIDAD_6_MAS("6° ciclo o más universitario");

    private final String displayName;

    AcademicGrade(String displayName) {
        this.displayName = displayName;
    }

    public String getCode() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<AcademicGrade> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(grade -> grade.name().equalsIgnoreCase(code.trim()))
                .findFirst();
    }
}
