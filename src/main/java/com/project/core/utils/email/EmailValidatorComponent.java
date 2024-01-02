package com.project.core.utils.email;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EmailValidatorComponent implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target == null) {
            return;
        }

        String email = (String) target;

        if (!EmailValidator.isValid(email)) {
            errors.reject("Email", "Formato de e-mail é inválido");
        }
    }
}
