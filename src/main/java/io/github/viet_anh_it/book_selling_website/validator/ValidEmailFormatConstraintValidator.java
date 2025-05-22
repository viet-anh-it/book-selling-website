package io.github.viet_anh_it.book_selling_website.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.viet_anh_it.book_selling_website.validator.annotation.ValidEmailFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidEmailFormatConstraintValidator implements ConstraintValidator<ValidEmailFormat, String> {

    Pattern pattern;
    Matcher matcher;
    static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        if (value.isBlank()) {
            return false;
        }

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(value);
        return matcher.matches();
    }

}
