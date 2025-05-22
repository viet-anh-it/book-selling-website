package io.github.viet_anh_it.book_selling_website.validator;

import io.github.viet_anh_it.book_selling_website.service.BookService;
import io.github.viet_anh_it.book_selling_website.validator.annotation.BookIdExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookIdExistValidator implements ConstraintValidator<BookIdExist, Long> {

    BookService bookService;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (this.bookService.fetchSingleBookById(value).isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Book id is not existed in the database.")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}
