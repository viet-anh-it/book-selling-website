package io.github.viet_anh_it.book_selling_website.service;

import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.dto.request.LogInRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.request.ResetPasswordRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.request.SendEmailRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.request.SignUpRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.LogInResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.RefreshTokenResponseDTO;

public interface AuthenticationService {

    void signUp(SignUpRequestDTO signUpRequestDTO);

    LogInResponseDTO logIn(LogInRequestDTO signUpRequestDTO, Optional<String> optionalRefreshTokenString);

    RefreshTokenResponseDTO refreshToken(Optional<String> optionalRefreshTokenString);

    void logOut(Optional<String> optionalRefreshTokenString);

    void revokeRefreshToken(Optional<String> optionalRefreshTokenString);

    void activateAccount(Optional<String> optionalAccountActivationTokenString);

    void sendActivateAccountEmail(SendEmailRequestDTO sendEmailRequestDTO);

    void sendForgotPasswordEmail(SendEmailRequestDTO sendEmailRequestDTO);

    String validateForgotPasswordToken(Optional<String> optionalToken);

    void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}
