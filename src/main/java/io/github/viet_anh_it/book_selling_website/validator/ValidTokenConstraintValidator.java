package io.github.viet_anh_it.book_selling_website.validator;

import java.time.Instant;
import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.enums.TokenTypeEnum;
import io.github.viet_anh_it.book_selling_website.model.VerificationToken;
import io.github.viet_anh_it.book_selling_website.service.VerificationTokenService;
import io.github.viet_anh_it.book_selling_website.validator.annotation.ValidToken;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidTokenConstraintValidator implements ConstraintValidator<ValidToken, String> {

    @NonFinal
    TokenTypeEnum tokenType;
    VerificationTokenService verificationTokenService;

    @Override
    public void initialize(ValidToken constraintAnnotation) {
        this.tokenType = constraintAnnotation.tokenType();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        if (value.isBlank()) {
            return false;
        }

        Optional<VerificationToken> optionalToken = this.verificationTokenService.findByTokenValue(value);
        if (optionalToken.isEmpty()) {
            return false;
        }

        VerificationToken token = optionalToken.get();
        if (!this.tokenType.name().equals(token.getType().name())) {
            return false;
        }

        if (token.getUser() == null) {
            return false;
        }

        if (Instant.now().isAfter(token.getExpiresAt())) {
            return false;
        }

        return true;
    }

}
