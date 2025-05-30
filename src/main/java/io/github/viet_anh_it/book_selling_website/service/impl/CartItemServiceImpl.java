package io.github.viet_anh_it.book_selling_website.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import io.github.viet_anh_it.book_selling_website.repository.CartItemRepository;
import io.github.viet_anh_it.book_selling_website.repository.CartRepository;
import io.github.viet_anh_it.book_selling_website.service.CartItemService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemServiceImpl implements CartItemService {

    UserService userService;
    BookRepository bookRepository;
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;

    @Override
    @PreAuthorize("hasAuthority('ADD_TO_CART')")
    public CartItem save(CartItem cartItem) {
        return this.cartItemRepository.save(cartItem);
    }

    @Override
    public Optional<CartItem> findIfBookAlreadyInCart(long userId, long bookId) {
        return this.cartItemRepository.findIfBookAlreadyInCart(userId, bookId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('DELETE_CART_ITEM')")
    public SuccessResponse<Void> deleteCartItemById(long cartItemId) {
        Optional<CartItem> optCartItem = this.cartItemRepository.findById(cartItemId);
        if (optCartItem.isPresent()) {
            CartItem cartItem = optCartItem.get();
            Book book = cartItem.getBook();
            book.setStockQuantity(book.getStockQuantity() + cartItem.getQuantity());
            this.bookRepository.save(book);
            Cart cart = cartItem.getCart();
            cart.setTotalPrice(cart.getTotalPrice() - cartItem.getTotalPrice());
            this.cartRepository.save(cart);
            this.cartItemRepository.deleteById(cartItemId);
        }
        return SuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Xóa sản phẩm khỏi giỏ hàng thành công!")
                .build();
    }

    @Override
    public SuccessResponse<List<CartItemDTO>> getAllCartItems() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentLoggedInUser = this.userService.findByEmail(username).get();
        List<CartItem> cartItems = currentLoggedInUser
                .getCart()
                .getCartItems()
                .stream()
                .toList();
        List<CartItemDTO> cartItemDtos = cartItems
                .stream()
                .map(cartItem -> CartItemDTO
                        .builder()
                        .bookId(cartItem.getBook().getId())
                        .cartItemId(cartItem.getId())
                        .image(cartItem.getImage())
                        .name(cartItem.getName())
                        .price(cartItem.getPrice())
                        .addToCartQuantity(cartItem.getQuantity())
                        .totalPrice(cartItem.getTotalPrice())
                        .build())
                .toList();
        SuccessResponse<List<CartItemDTO>> successResponse = SuccessResponse
                .<List<CartItemDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy sản phẩm trong giỏ hàng thành công!")
                .data(cartItemDtos)
                .build();
        return successResponse;
    }

    @Override
    @Transactional
    public SuccessResponse<CartDTO> updateCartItem(long cartItemId, CartItemDTO cartItemDTO) {
        Optional<CartItem> optCartItem = this.cartItemRepository.findById(cartItemId);
        SuccessResponse<CartDTO> successResponse = SuccessResponse.<CartDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật giỏ hàng thành công!")
                .build();
        if (optCartItem.isPresent()) {
            CartItem cartItem = optCartItem.get();
            int quantityDifference = Math.abs(cartItemDTO.getAddToCartQuantity() - cartItem.getQuantity());
            int priceDifference = cartItemDTO.getTotalPrice() - cartItem.getTotalPrice();
            cartItem.setQuantity(cartItemDTO.getAddToCartQuantity());
            cartItem.setTotalPrice(cartItemDTO.getTotalPrice());
            this.cartItemRepository.save(cartItem);
            Cart cart = cartItem.getCart();
            Book book = cartItem.getBook();
            if (priceDifference > 0) { // user ấn nút tăng
                book.setStockQuantity(book.getStockQuantity() - quantityDifference);
                this.bookRepository.save(book);
                cart.setTotalPrice(cart.getTotalPrice() + priceDifference);
                this.cartRepository.save(cart);
                successResponse.setData(new CartDTO(cart.getTotalPrice(), cart.getUniqueProductCount()));
                return successResponse;
            } else if (priceDifference < 0) { // user ấn nút giảm
                book.setStockQuantity(book.getStockQuantity() + quantityDifference);
                this.bookRepository.save(book);
                cart.setTotalPrice(cart.getTotalPrice() - (-priceDifference));
                this.cartRepository.save(cart);
                successResponse.setData(new CartDTO(cart.getTotalPrice(), cart.getUniqueProductCount()));
                return successResponse;
            }
        }
        successResponse.setData(null);
        return successResponse;
    }

}
