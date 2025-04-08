package io.github.viet_anh_it.book_selling_website.exception;

import org.springframework.security.oauth2.jwt.JwtException;

public class RefreshTokenException extends JwtException {

    public RefreshTokenException(String message) {
        super(message);
    }

}
