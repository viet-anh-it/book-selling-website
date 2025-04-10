package io.github.viet_anh_it.book_selling_website.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;

@RestController
public class CartController {

    @PutMapping("/cart/add")
    public ResponseEntity<SuccessResponse<Object>> addToCart() {
        SuccessResponse<Object> successResponse = SuccessResponse.<Object>builder()
                .status(HttpStatus.OK.value())
                .message("Thêm sản phẩm vào giỏ hàng thành công!")
                .data(SecurityContextHolder.getContext().getAuthentication())
                .build();
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(successResponse);
    }
}
