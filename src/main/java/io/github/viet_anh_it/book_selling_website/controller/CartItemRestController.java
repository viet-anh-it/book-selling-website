package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.viet_anh_it.book_selling_website.dto.CartDTO;
import io.github.viet_anh_it.book_selling_website.dto.CartItemDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.service.CartItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemRestController {

    CartItemService cartItemService;

    @Secured({ "ROLE_CUSTOMER" })
    @DeleteMapping("/cartitems/{cartItemId}")
    public ResponseEntity<SuccessResponse<Void>> deleteCartItemById(@PathVariable long cartItemId) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.cartItemService.deleteCartItemById(cartItemId));
    }

    @Secured({ "ROLE_CUSTOMER" })
    @GetMapping("/cartitems")
    public ResponseEntity<SuccessResponse<List<CartItemDTO>>> getAllCartItems() {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.cartItemService.getAllCartItems());
    }

    @Secured({ "ROLE_CUSTOMER" })
    @PatchMapping("/cartitems/{cartItemId}")
    public ResponseEntity<SuccessResponse<CartDTO>> updateCartItemById(@PathVariable long cartItemId,
            @RequestBody CartItemDTO cartItemDTO) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.cartItemService.updateCartItem(cartItemId, cartItemDTO));
    }
}
