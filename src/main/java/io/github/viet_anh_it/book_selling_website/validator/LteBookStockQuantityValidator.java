package io.github.viet_anh_it.book_selling_website.validator;

import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.dto.BookDTO;
import io.github.viet_anh_it.book_selling_website.dto.CartItemDTO;
import io.github.viet_anh_it.book_selling_website.service.BookService;
import io.github.viet_anh_it.book_selling_website.validator.annotation.LteBookStockQuantity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LteBookStockQuantityValidator implements ConstraintValidator<LteBookStockQuantity, CartItemDTO> {

    BookService bookService;

    @Override
    public boolean isValid(CartItemDTO cartItemDTO, ConstraintValidatorContext context) {
        Optional<BookDTO> bookDto = this.bookService.fetchSingleBookById(cartItemDTO.getBookId());
        if (cartItemDTO.getAddToCartQuantity().intValue() > bookDto.get().getStockQuantity()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Chỉ còn " + bookDto.get().getStockQuantity() + " sản phẩm!")
                    .addPropertyNode("addToCartQuantity")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}
