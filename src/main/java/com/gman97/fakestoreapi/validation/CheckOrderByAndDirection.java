package com.gman97.fakestoreapi.validation;

import com.gman97.fakestoreapi.validation.impl.CheckOrderByAndDirectionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = CheckOrderByAndDirectionValidator.class)
public @interface CheckOrderByAndDirection {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
