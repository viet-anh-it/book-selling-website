package io.github.viet_anh_it.book_selling_website.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.dto.request.LogInRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.request.SignUpRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.LogInResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.RefreshTokenResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SignUpResponseDTO;
import io.github.viet_anh_it.book_selling_website.enums.RoleEnum;
import io.github.viet_anh_it.book_selling_website.enums.TokenTypeEnum;
import io.github.viet_anh_it.book_selling_website.exception.EmailAlreadyExistedException;
import io.github.viet_anh_it.book_selling_website.exception.RefreshTokenException;
import io.github.viet_anh_it.book_selling_website.exception.RoleNotFoundException;
import io.github.viet_anh_it.book_selling_website.model.BlackListedAccessToken;
import io.github.viet_anh_it.book_selling_website.model.RefreshToken;
import io.github.viet_anh_it.book_selling_website.model.Role;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.service.AuthService;
import io.github.viet_anh_it.book_selling_website.service.BlackListedAccessTokenService;
import io.github.viet_anh_it.book_selling_website.service.JwtService;
import io.github.viet_anh_it.book_selling_website.service.RefreshTokenService;
import io.github.viet_anh_it.book_selling_website.service.RoleService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

        @NonFinal
        @Value("${jwt.secret-key}")
        String jwtSecretKey;

        @NonFinal
        @Value("${jwt.access-token.validity-duration}")
        long accessTokenValidityDuration;

        @NonFinal
        @Value("${jwt.refresh-token.validity-duration}")
        long refreshTokenValidityDuration;

        UserService userService;
        PasswordEncoder passwordEncoder;
        AuthenticationManager authenticationManager;
        JwtService jwtService;
        BlackListedAccessTokenService blackListedAccessTokenService;
        RefreshTokenService refreshTokenService;
        RoleService roleService;

        @Qualifier("accessTokenJwtDecoder")
        JwtDecoder accessTokenJwtDecoder;

        @Qualifier("refreshTokenJwtDecoder")
        JwtDecoder refreshTokenJwtDecoder;

        @Override
        public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
                if (this.userService.existsByEmail(signUpRequestDTO.getEmail())) {
                        throw new EmailAlreadyExistedException("Email đã tồn tại!");
                }

                String hashedPassword = this.passwordEncoder.encode(signUpRequestDTO.getPassword());
                Role roleEntity = this.roleService.findByName(RoleEnum.CUSTOMER)
                                .orElseThrow(() -> new RoleNotFoundException("Vai trò không tồn tại!"));
                User user = User
                                .builder()
                                .email(signUpRequestDTO.getEmail())
                                .password(hashedPassword)
                                .role(roleEntity)
                                .build();
                user = this.userService.save(user);

                SignUpResponseDTO signUpResponseDTO = SignUpResponseDTO
                                .builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .build();

                return signUpResponseDTO;
        }

        @Override
        public LogInResponseDTO logIn(LogInRequestDTO logInRequestDTO,
                        Optional<String> optionalOldRefreshTokenString) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken
                                .unauthenticated(logInRequestDTO.getEmail(), logInRequestDTO.getPassword());
                Authentication authentication = this.authenticationManager
                                .authenticate(usernamePasswordAuthenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                Jwt newAccessTokenJwtObject = this.jwtService.createJwt(authentication.getName(),
                                this.accessTokenValidityDuration, TokenTypeEnum.ACCESS.getName());
                Jwt newRefreshTokenJwtObject = this.jwtService.createJwt(authentication.getName(),
                                this.refreshTokenValidityDuration, TokenTypeEnum.REFRESH.getName());

                User user = this.userService.findByEmail(authentication.getName()).get();
                if (optionalOldRefreshTokenString.isPresent()) {
                        String oldRefreshTokenString = optionalOldRefreshTokenString.get();
                        this.refreshTokenService.findByTokenValueAndUserId(oldRefreshTokenString, user.getId())
                                        .ifPresentOrElse(refreshTokenEntity -> {
                                                refreshTokenEntity.setTokenValue(
                                                                newRefreshTokenJwtObject.getTokenValue());
                                                refreshTokenEntity.setJti(newRefreshTokenJwtObject.getId());
                                                refreshTokenEntity
                                                                .setExpiresAt(newRefreshTokenJwtObject.getExpiresAt());
                                                this.refreshTokenService.save(refreshTokenEntity);
                                        }, () -> {
                                                RefreshToken refreshTokenEntity = RefreshToken
                                                                .builder()
                                                                .tokenValue(newRefreshTokenJwtObject.getTokenValue())
                                                                .jti(newRefreshTokenJwtObject.getId())
                                                                .expiresAt(newRefreshTokenJwtObject.getExpiresAt())
                                                                .user(user)
                                                                .build();
                                                this.refreshTokenService.save(refreshTokenEntity);
                                        });
                } else {
                        RefreshToken refreshTokenEntity = RefreshToken
                                        .builder()
                                        .tokenValue(newRefreshTokenJwtObject.getTokenValue())
                                        .jti(newRefreshTokenJwtObject.getId())
                                        .expiresAt(newRefreshTokenJwtObject.getExpiresAt())
                                        .user(user)
                                        .build();
                        this.refreshTokenService.save(refreshTokenEntity);
                }

                LogInResponseDTO logInResponseDTO = LogInResponseDTO.builder()
                                .accessToken(newAccessTokenJwtObject.getTokenValue())
                                .refreshToken(newRefreshTokenJwtObject.getTokenValue())
                                .build();

                return logInResponseDTO;
        }

        @Override
        public void logOut(Optional<String> optionalOldRefreshTokenString) {
                Jwt accessTokenJwtObject = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String userEmail = accessTokenJwtObject.getSubject();
                User user = this.userService.findByEmail(userEmail).get();

                BlackListedAccessToken blackListedAccessToken = BlackListedAccessToken.builder()
                                .jti(accessTokenJwtObject.getId())
                                .expiresAt(accessTokenJwtObject.getExpiresAt())
                                .user(user)
                                .build();
                this.blackListedAccessTokenService.save(blackListedAccessToken);

                if (optionalOldRefreshTokenString.isPresent()) {
                        String oldRefreshTokenString = optionalOldRefreshTokenString.get();
                        this.refreshTokenService
                                        .findByTokenValueAndUserId(oldRefreshTokenString, user.getId())
                                        .ifPresentOrElse(refreshTokenEntity -> this.refreshTokenService
                                                        .delete(refreshTokenEntity),
                                                        () -> this.refreshTokenService
                                                                        .deleteAll(user.getRefreshTokens()));
                } else {
                        this.refreshTokenService.deleteAll(user.getRefreshTokens());
                }
        }

        @Override
        public RefreshTokenResponseDTO refreshToken(Optional<String> optionalOldRefreshTokenString) {
                try {
                        String oldRefreshTokenString = optionalOldRefreshTokenString
                                        .orElseThrow(() -> new RefreshTokenException("Refresh token không hợp lệ!"));
                        Jwt oldRefreshTokenJwtObject = this.refreshTokenJwtDecoder.decode(oldRefreshTokenString);

                        String username = oldRefreshTokenJwtObject.getSubject();
                        Jwt newAccessTokenJwtObject = this.jwtService.createJwt(username,
                                        this.accessTokenValidityDuration, TokenTypeEnum.ACCESS.getName());
                        long remainingValidSeconds = Duration
                                        .between(Instant.now(), oldRefreshTokenJwtObject.getExpiresAt()).getSeconds();
                        Jwt newRefreshTokenJwtObject = this.jwtService.createJwt(username, remainingValidSeconds,
                                        TokenTypeEnum.REFRESH.getName());

                        User user = this.userService.findByEmail(username).get();
                        RefreshToken refreshTokenEntity = this.refreshTokenService.findByTokenValueAndUserIdAndJti(
                                        oldRefreshTokenString, user.getId(), oldRefreshTokenJwtObject.getId()).get();
                        refreshTokenEntity.setTokenValue(newRefreshTokenJwtObject.getTokenValue());
                        refreshTokenEntity.setJti(newRefreshTokenJwtObject.getId());
                        refreshTokenEntity.setExpiresAt(newRefreshTokenJwtObject.getExpiresAt());
                        refreshTokenEntity.setUser(user);
                        this.refreshTokenService.save(refreshTokenEntity);

                        RefreshTokenResponseDTO refreshTokenResponseDTO = RefreshTokenResponseDTO.builder()
                                        .accessToken(newAccessTokenJwtObject)
                                        .refreshToken(newRefreshTokenJwtObject)
                                        .build();

                        return refreshTokenResponseDTO;
                } catch (JwtException exception) {
                        throw new RefreshTokenException(exception.getMessage());
                }
        }

        @Override
        public void revokeRefreshToken(Optional<String> optionalOldRefreshTokenString) {
                if (optionalOldRefreshTokenString.isPresent()) {
                        String oldRefreshTokenString = optionalOldRefreshTokenString.get();
                        Optional<RefreshToken> optionalRefreshTokenEntity = this.refreshTokenService
                                        .findByTokenValue(oldRefreshTokenString);
                        if (optionalRefreshTokenEntity.isPresent()) {
                                RefreshToken refreshTokenEntity = optionalRefreshTokenEntity.get();
                                this.refreshTokenService.delete(refreshTokenEntity);
                        }
                }
        }

}
