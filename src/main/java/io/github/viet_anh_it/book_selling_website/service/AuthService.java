package io.github.viet_anh_it.book_selling_website.service;

import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.dto.request.LogInRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.request.SignUpRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.LogInResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.RefreshTokenResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SignUpResponseDTO;

public interface AuthService {

    SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO);

    LogInResponseDTO logIn(LogInRequestDTO signUpRequestDTO, Optional<String> optionalRefreshToken);

    RefreshTokenResponseDTO refreshToken(Optional<String> optionalRefreshToken);

    void logOut(Optional<String> optionalRefreshToken);

    void revokeRefreshToken(Optional<String> optionalRefreshToken);
}
