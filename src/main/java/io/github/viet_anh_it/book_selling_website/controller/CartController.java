package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.viet_anh_it.book_selling_website.model.Cart;
import io.github.viet_anh_it.book_selling_website.model.CartItem;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    UserService userService;

    @Secured({ "ROLE_CUSTOMER" })
    @GetMapping("/cart")
    public String getCartPage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentLoggedInUser = this.userService.findByEmail(username).get();
        Optional<Cart> optCart = Optional.ofNullable(currentLoggedInUser.getCart());
        List<CartItem> cartItems = optCart.isPresent() ? optCart.get().getCartItems().stream().toList() : null;
        model.addAttribute("cart", optCart.isPresent() ? optCart.get() : new Cart());
        model.addAttribute("cartItems", cartItems);
        return "cart";
    }
}
