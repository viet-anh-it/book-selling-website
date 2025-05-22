package io.github.viet_anh_it.book_selling_website.exception.handler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.viet_anh_it.book_selling_website.exception.ActivateAccountTokenException;
import io.github.viet_anh_it.book_selling_website.exception.VerificationTokenException;

@ControllerAdvice(annotations = Controller.class)
public class MvcGlobalExceptionHandler {

    @ExceptionHandler({ VerificationTokenException.class })
    public String handleVerificationTokenException(RedirectAttributes redirectAttributes,
            VerificationTokenException exception) {
        if (exception instanceof ActivateAccountTokenException) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/signUp";
        } else {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/logIn";
        }
    }
}
