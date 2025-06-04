package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.viet_anh_it.book_selling_website.dto.UserDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.User_;
import io.github.viet_anh_it.book_selling_website.repository.UserRepository;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;
    UserRepository userRepository;

    @Secured({ "ROLE_ADMIN" })
    @GetMapping("/users")
    public String getBooksPage(Model model, @PageableDefault(page = 0, size = 6, sort = User_.EMAIL, direction = Direction.ASC) Pageable pageable) {
        SuccessResponse<List<UserDTO>> successResponse = this.userService.getAllUsers(pageable);
        model.addAttribute("users", successResponse.getData());
        model.addAttribute("paginationMetadata", successResponse.getPaginationMetadata());
        return "manageAccounts";
    }
}
