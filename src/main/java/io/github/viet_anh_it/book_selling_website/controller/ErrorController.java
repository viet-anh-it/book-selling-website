package io.github.viet_anh_it.book_selling_website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/403Forbidden")
    public String get403ForbiddenPage() {
        return "403Forbidden";
    }

    @GetMapping("/401Unauthorized")
    public String get401UnauthorizedPage() {
        return "401Unauthorized";
    }
}
