package io.github.viet_anh_it.book_selling_website.custom;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtAuthenticationConverterImpl implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    @Nullable
    @SuppressWarnings("null")
    public AbstractAuthenticationToken convert(Jwt jwt) {
        return null;
    }

}
