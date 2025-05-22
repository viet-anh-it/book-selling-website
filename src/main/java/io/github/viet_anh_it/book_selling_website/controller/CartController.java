package io.github.viet_anh_it.book_selling_website.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @Secured({ "ROLE_CUSTOMER" })
    @GetMapping("/cart")
    public String getCartPage() {
        return "cart";
    }
}
