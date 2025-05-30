package io.github.viet_anh_it.book_selling_website.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.viet_anh_it.book_selling_website.dto.CartDTO;
import io.github.viet_anh_it.book_selling_website.dto.CartItemDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.service.CartService;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.OnCreate;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.ValidationOrder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartRestController {

    CartService cartService;

    @Secured({ "ROLE_CUSTOMER" })
    @PostMapping("/carts/cartitem")
    public ResponseEntity<SuccessResponse<Void>> addToCart(
            @RequestBody @Validated({ ValidationOrder.class, OnCreate.class }) CartItemDTO cartItemDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        this.cartService.addToCart(authentication, cartItemDTO);
        SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .message("Thêm cuốn sách vào giỏ hàng thành công!")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    @Secured({ "ROLE_CUSTOMER" })
    @GetMapping("/carts")
    public ResponseEntity<SuccessResponse<CartDTO>> getCart() {
        return ResponseEntity.status(HttpStatus.OK).body(this.cartService.getCart());
    }
}
