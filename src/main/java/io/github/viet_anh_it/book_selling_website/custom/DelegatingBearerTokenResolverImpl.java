package io.github.viet_anh_it.book_selling_website.custom;

import java.util.Set;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DelegatingBearerTokenResolverImpl implements BearerTokenResolver {

    Set<BearerTokenResolver> bearerTokenResolvers;

    @Override
    public String resolve(HttpServletRequest request) {
        for (BearerTokenResolver bearerTokenResolver : this.bearerTokenResolvers) {
            String bearerToken = bearerTokenResolver.resolve(request);
            if (bearerToken != null) {
                return bearerToken;
            }
        }
        return null;
    }

}
