package io.github.viet_anh_it.book_selling_website.dto.response;

import java.io.Serializable;

import org.springframework.security.oauth2.jwt.Jwt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenResponseDTO implements Serializable {

    Jwt accessToken;
    Jwt refreshToken;
}
