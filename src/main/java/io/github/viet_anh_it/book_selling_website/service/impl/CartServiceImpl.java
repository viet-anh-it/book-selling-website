package io.github.viet_anh_it.book_selling_website.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.viet_anh_it.book_selling_website.dto.CartDTO;
import io.github.viet_anh_it.book_selling_website.dto.CartItemDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
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
            this.cartRepository.save(cart);
        }
        Book book = this.bookRepository.findById(cartItemDTO.getBookId()).get();
        Optional<CartItem> optCartItem = this.cartItemService.findIfBookAlreadyInCart(currentLoggedInUser.getId(), book.getId());
        CartItem cartItem = new CartItem(); // transient
        if (optCartItem.isPresent()) {
            cartItem = optCartItem.get();
        }
        cartItem.setImage(book.getImage().substring(book.getImage().indexOf("\\book-covers")));
        cartItem.setName(book.getName());
        cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getAddToCartQuantity());
        cartItem.setPrice(book.getPrice());
        cartItem.setTotalPrice(cartItem.getTotalPrice() + book.getPrice() * cartItemDTO.getAddToCartQuantity());
        cartItem.setCart(cart);
        cartItem.setBook(book);

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

        book.setStockQuantity(book.getStockQuantity() - cartItemDTO.getAddToCartQuantity());
        this.bookRepository.save(book);

        CartDTO cartDTO = new CartDTO();
        cartDTO.setTotalUniqueProduct(cart.getUniqueProductCount());
        return cartDTO;
    }

    @Override
    public CartDTO save(Cart cart) {
        this.cartRepository.save(cart);
        return new CartDTO(cart.getTotalPrice(), cart.getUniqueProductCount());
    }

    @Override
    public SuccessResponse<CartDTO> getCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentLoggedInUser = this.userService.findByEmail(username).get();
        Cart cart = currentLoggedInUser.getCart();
        CartDTO cartDto = new CartDTO(cart.getTotalPrice(), cart.getUniqueProductCount());
        return SuccessResponse.<CartDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy giỏ hàng thành công!")
                .data(cartDto)
                .build();
    }

}
