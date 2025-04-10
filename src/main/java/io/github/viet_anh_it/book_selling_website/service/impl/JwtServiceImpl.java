package io.github.viet_anh_it.book_selling_website.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.service.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtServiceImpl implements JwtService {

        JwtEncoder jwtEncoder;

        @Override
        public Jwt createJwt(String username, long validityDuration, String usage) {
                JwsHeader jwsHeader = JwsHeader
                                .with(MacAlgorithm.HS256)
                                .type("jwt")
                                .build();

                JwtClaimsSet jwtClaimsSet = JwtClaimsSet
                                .builder()
                                .id(UUID.randomUUID().toString())
                                .issuedAt(Instant.now())
                                .expiresAt(Instant.now().plusSeconds(validityDuration))
                                .subject(username)
                                .claim("usage", usage)
                                .build();

                JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters
                                .from(jwsHeader, jwtClaimsSet);
                Jwt jwt = this.jwtEncoder.encode(jwtEncoderParameters);

                return jwt;
        }
}
