package io.github.viet_anh_it.book_selling_website.service;

public interface JwtService {

    String createJwt(String username, long validityDuration, String usage);
}
