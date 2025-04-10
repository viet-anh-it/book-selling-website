package io.github.viet_anh_it.book_selling_website.service;

import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {

    Jwt createJwt(String username, long validityDuration, String usage);
}
