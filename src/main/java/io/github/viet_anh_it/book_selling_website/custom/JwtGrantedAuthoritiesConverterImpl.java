package io.github.viet_anh_it.book_selling_website.custom;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtGrantedAuthoritiesConverterImpl implements Converter<Jwt, Collection<GrantedAuthority>> {

    UserDetailsService userDetailsService;

    @Override
    @Nullable
    @SuppressWarnings("null")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        String userEmail = jwt.getSubject();
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        Collection<GrantedAuthority> authorities = new HashSet<>(userDetails.getAuthorities());
        return authorities;
    }

}
