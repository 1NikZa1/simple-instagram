package com.nikza.socialnetwork.annotations;

import com.nikza.socialnetwork.validations.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
    String message() default "password do not matcher";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
