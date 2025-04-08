package io.github.viet_anh_it.book_selling_website.custom;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CookieBearerTokenResolverImpl implements BearerTokenResolver {

    String[] whitelist;
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    String accessTokenCookieName = "access_token";
    String defaultEmptyAccessToken = "unknown_access_token";

    public CookieBearerTokenResolverImpl(String[] whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public String resolve(HttpServletRequest request) {
        for (String publicEndpointUrl : this.whitelist) {
            if (this.antPathMatcher.match(publicEndpointUrl, request.getRequestURI())) {
                return null;
            }
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (this.accessTokenCookieName.equals(cookie.getName())) {
                    String accessTokenCookieValue = cookie.getValue();
                    if (accessTokenCookieValue != null && !accessTokenCookieValue.isBlank()) {
                        return accessTokenCookieValue;
                    } else {
                        return this.defaultEmptyAccessToken;
                    }
                }
            }
        }

        return null;
    }
}
