package io.github.viet_anh_it.book_selling_website.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoryController {

    @Secured({ "ROLE_MANAGER" })
    @GetMapping("/categories")
    public String getManageCategoryPage() {
        return "manageCategories";
    }
}
