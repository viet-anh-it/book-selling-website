package io.github.viet_anh_it.book_selling_website.validator;

import io.github.viet_anh_it.book_selling_website.service.UserService;
import io.github.viet_anh_it.book_selling_website.validator.annotation.AccountAlreadyActive;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountAlreadyActiveConstraintValidator implements ConstraintValidator<AccountAlreadyActive, String> {

    UserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (this.userService.findByEmail(value).get().isActive()) {
            return false;
        }
        return true;
    }

}
