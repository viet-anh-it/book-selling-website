package io.github.viet_anh_it.book_selling_website.service;

import java.util.List;
import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.dto.CartDTO;
import io.github.viet_anh_it.book_selling_website.dto.CartItemDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.CartItem;

public interface CartItemService {

    CartItem save(CartItem cartItem);

    Optional<CartItem> findIfBookAlreadyInCart(long userId, long bookId);

    SuccessResponse<Void> deleteCartItemById(long cartItemId);

    SuccessResponse<List<CartItemDTO>> getAllCartItems();

    SuccessResponse<CartDTO> updateCartItem(long cartItemId, CartItemDTO cartItemDTO);
}
