package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.viet_anh_it.book_selling_website.dto.CartDTO;
import io.github.viet_anh_it.book_selling_website.dto.CartItemDTO;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.service.CartItemService;
import io.github.viet_anh_it.book_selling_website.service.CartService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckoutController {

    UserService userService;
    CartService cartService;
    CartItemService cartItemService;

    @Secured({ "ROLE_CUSTOMER" })
    @GetMapping("/checkout")
    public String getCheckoutPage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentLoggedInUser = this.userService.findByEmail(username).get();
        model.addAttribute("user", currentLoggedInUser);
        List<CartItemDTO> cartItemDtos = this.cartItemService.getAllCartItems().getData();
        model.addAttribute("cartItems", cartItemDtos);
        CartDTO cartDto = this.cartService.getCart().getData();
        model.addAttribute("cart", cartDto);
        return "checkout";
    }
}
