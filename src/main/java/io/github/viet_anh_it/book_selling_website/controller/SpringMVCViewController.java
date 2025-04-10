package io.github.viet_anh_it.book_selling_website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpringMVCViewController {

    @GetMapping("/sign-up")
    public String getSignUpPage() {
        return "sign-up";
    }

    @GetMapping("/log-in")
    public String getLogInPage() {
        return "log-in";
    }

    @GetMapping("/cart")
    public String getCartPage() {
        return "cart";
    }

    @GetMapping("/home")
    public String getHomePage() {
        return "home";
    }
}
