package io.github.viet_anh_it.book_selling_website.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.viet_anh_it.book_selling_website.validator.ValidEmailFormatConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ValidEmailFormatConstraintValidator.class })
public @interface ValidEmailFormat {

    String message() default "Email không đúng định dạng!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
