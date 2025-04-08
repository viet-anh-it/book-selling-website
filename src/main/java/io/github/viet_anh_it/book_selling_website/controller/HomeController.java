package io.github.viet_anh_it.book_selling_website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String getHomePage() {
        return "home";
    }

    @ResponseBody
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, client!";
    }
}
