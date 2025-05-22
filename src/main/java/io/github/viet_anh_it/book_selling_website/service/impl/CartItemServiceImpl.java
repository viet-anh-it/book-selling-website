package io.github.viet_anh_it.book_selling_website.service.impl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.model.CartItem;
import io.github.viet_anh_it.book_selling_website.repository.CartItemRepository;
import io.github.viet_anh_it.book_selling_website.service.CartItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemServiceImpl implements CartItemService {

    CartItemRepository cartItemRepository;

    @PreAuthorize("hasAuthority('ADD_TO_CART')")
    @Override
    public CartItem save(CartItem cartItem) {
        return this.cartItemRepository.save(cartItem);
    }
}
