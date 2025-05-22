package io.github.viet_anh_it.book_selling_website.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.viet_anh_it.book_selling_website.dto.CartDTO;
import io.github.viet_anh_it.book_selling_website.dto.CartItemDTO;
import io.github.viet_anh_it.book_selling_website.model.Book;
import io.github.viet_anh_it.book_selling_website.model.Cart;
import io.github.viet_anh_it.book_selling_website.model.CartItem;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.repository.BookRepository;
import io.github.viet_anh_it.book_selling_website.repository.CartRepository;
import io.github.viet_anh_it.book_selling_website.service.CartItemService;
import io.github.viet_anh_it.book_selling_website.service.CartService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

    UserService userService;
    BookRepository bookRepository;
    CartRepository cartRepository;
    CartItemService cartItemService;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADD_TO_CART')")
    public CartDTO addToCart(Authentication authentication, CartItemDTO cartItemDTO) {
        User currentLoggedInUser = this.userService.findByEmail(authentication.getName()).get(); // managed
        Cart cart = currentLoggedInUser.getCart();
        if (cart == null) {
            cart = new Cart(); // transient
            cart.setUser(currentLoggedInUser);
        }
        Book book = this.bookRepository.findById(cartItemDTO.getBookId()).get();
        CartItem cartItem = new CartItem(); // transient
        cartItem.setImage(book.getImage().substring(book.getImage().indexOf("\\book-covers")));
        cartItem.setName(book.getName());
        cartItem.setQuantity(cartItemDTO.getAddToCartQuantity());
        cartItem.setPrice(book.getPrice());
        cartItem.setTotalPrice(book.getPrice() * cartItemDTO.getAddToCartQuantity());
        cartItem.setCart(cart);
        cartItem.setProduct(book);

        Set<CartItem> cartItemSet = cart.getCartItems();
        if (cartItemSet == null) {
            cartItemSet = new HashSet<>();
            cartItemSet.add(cartItem);
            cart.setCartItems(cartItemSet);
        } else {
            cartItemSet.add(cartItem);
        }

        cart.setUniqueProductCount(cartItemSet.size());
        cart.setTotalPrice(cart.getTotalPrice() + cartItem.getTotalPrice());
        this.cartRepository.save(cart);
        this.cartItemService.save(cartItem);

        CartDTO cartDTO = new CartDTO();
        cartDTO.setTotalUniqueProduct(cart.getUniqueProductCount());
        return cartDTO;
    }

}
