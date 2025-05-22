package io.github.viet_anh_it.book_selling_website.validator;

import java.lang.reflect.Field;

import io.github.viet_anh_it.book_selling_website.validator.annotation.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordMatchConstraintValidator implements ConstraintValidator<PasswordMatch, Object> {

    String firstPasswordFieldName;
    String secondPasswordFieldName;
    String message;

    @Override
    public void initialize(final PasswordMatch constraintAnnotation) {
        this.firstPasswordFieldName = constraintAnnotation.firstPasswordFieldName();
        this.secondPasswordFieldName = constraintAnnotation.secondPasswordFieldName();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            Field firstPasswordFieldObject = value.getClass().getDeclaredField(firstPasswordFieldName);
            firstPasswordFieldObject.setAccessible(true);
            String firstPasswordFieldValue = (String) firstPasswordFieldObject.get(value);

            Field secondPasswordFieldObject = value.getClass().getDeclaredField(secondPasswordFieldName);
            secondPasswordFieldObject.setAccessible(true);
            String secondPasswordFieldValue = (String) secondPasswordFieldObject.get(value);

            boolean valid = (firstPasswordFieldValue == null && secondPasswordFieldValue == null)
                    || (firstPasswordFieldValue != null && firstPasswordFieldValue.equals(secondPasswordFieldValue));

            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(secondPasswordFieldName)
                        .addConstraintViolation();
            }
            return valid;
        } catch (Exception exception) {
            return false;
        }
    }

}
