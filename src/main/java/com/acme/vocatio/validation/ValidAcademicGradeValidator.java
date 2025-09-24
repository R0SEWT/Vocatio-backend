package com.acme.vocatio.validation;

import com.acme.vocatio.model.AcademicGrade;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Revisa que el código recibido corresponda a un grado válido. */
public class ValidAcademicGradeValidator implements ConstraintValidator<ValidAcademicGrade, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return AcademicGrade.fromCode(value).isPresent();
    }
}
