package io.github.viet_anh_it.book_selling_website.service;

import org.springframework.security.core.Authentication;

import io.github.viet_anh_it.book_selling_website.dto.CartDTO;
import io.github.viet_anh_it.book_selling_website.dto.CartItemDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Cart;

public interface CartService {

    CartDTO addToCart(Authentication authentication, CartItemDTO cartItemDTO);

    CartDTO save(Cart cart);

    SuccessResponse<CartDTO> getCart();
}
