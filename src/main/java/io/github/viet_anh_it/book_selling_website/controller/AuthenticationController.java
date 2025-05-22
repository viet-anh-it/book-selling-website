package io.github.viet_anh_it.book_selling_website.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.viet_anh_it.book_selling_website.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @GetMapping("/signUp")
    public String getSignUpPage() {
        return "signUp";
    }

    @GetMapping("/logIn")
    public String getLogInPage() {
        return "logIn";
    }

    @GetMapping("/activateAccount")
    public String activateAccount(RedirectAttributes redirectAttributes,
            @RequestParam(name = "token") Optional<String> optionalToken) {
        this.authenticationService.activateAccount(optionalToken);
        String accountActivationSuccessMessage = "Kích hoạt tài khoản thành công! Đăng nhập tài khoản của bạn!";
        redirectAttributes.addFlashAttribute("message", accountActivationSuccessMessage);
        return "redirect:/logIn";
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword(Model model, @RequestParam(name = "token") Optional<String> optionalToken) {
        String token = this.authenticationService.validateForgotPasswordToken(optionalToken);
        model.addAttribute("token", token);
        return "forgotPassword";
    }
}
