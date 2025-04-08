package io.github.viet_anh_it.book_selling_website.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
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
        public SuccessResponse<SignUpResponseDTO> signUp(SignUpRequestDTO signUpRequestDTO) {
                if (this.userService.existsByEmail(signUpRequestDTO.getEmail())) {
                        throw new EmailAlreadyExistedException("Email đã tồn tại!");
                }

                String hashedPassword = this.passwordEncoder.encode(signUpRequestDTO.getPassword());
                Role roleEntity = this.roleService.findByName(RoleEnum.CUSTOMER)
                                .orElseThrow(() -> new RoleNotFoundException("Vai trò không tồn tại!"));
                User user = User.builder()
                                .email(signUpRequestDTO.getEmail())
                                .password(hashedPassword)
                                .role(roleEntity)
                                .build();
                user = this.userService.save(user);

                SignUpResponseDTO signUpResponseDTO = SignUpResponseDTO.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .build();

                SuccessResponse<SignUpResponseDTO> successResponse = SuccessResponse.<SignUpResponseDTO>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Đăng ký thành công!")
                                .data(signUpResponseDTO)
                                .build();

                return successResponse;
        }

        @Override
        public LogInResponseDTO logIn(LogInRequestDTO logInRequestDTO,
                        Optional<String> optionalRefreshToken) {
                UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                                .unauthenticated(logInRequestDTO.getEmail(), logInRequestDTO.getPassword());
                Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                String accessToken = this.jwtService.createJwt(authentication.getName(),
                                this.accessTokenValidityDuration, TokenTypeEnum.ACCESS.getName());
                String refreshToken = this.jwtService.createJwt(authentication.getName(),
                                this.refreshTokenValidityDuration, TokenTypeEnum.REFRESH.getName());

                Jwt refreshTokenJwtObject = this.refreshTokenJwtDecoder.decode(refreshToken);
                User user = this.userService.findByEmail(authentication.getName()).get();
                if (optionalRefreshToken.isPresent()) {
                        this.refreshTokenService.findByTokenValueAndUserId(optionalRefreshToken.get(), user.getId())
                                        .ifPresentOrElse(refreshTokenEntity -> {
                                                refreshTokenEntity.setTokenValue(refreshToken);
                                                refreshTokenEntity.setJti(refreshTokenJwtObject.getId());
                                                refreshTokenEntity.setExpiresAt(Instant.now()
                                                                .plusSeconds(this.refreshTokenValidityDuration));
                                                this.refreshTokenService.save(refreshTokenEntity);
                                        }, () -> {
                                                RefreshToken refreshTokenEntity = RefreshToken
                                                                .builder()
                                                                .tokenValue(refreshToken)
                                                                .jti(refreshTokenJwtObject.getId())
                                                                .expiresAt(Instant.now().plusSeconds(
                                                                                this.refreshTokenValidityDuration))
                                                                .user(user)
                                                                .build();
                                                this.refreshTokenService.save(refreshTokenEntity);
                                        });
                } else {
                        RefreshToken refreshTokenEntity = RefreshToken.builder()
                                        .tokenValue(refreshToken)
                                        .jti(refreshTokenJwtObject.getId())
                                        .expiresAt(Instant.now()
                                                        .plusSeconds(this.refreshTokenValidityDuration))
                                        .user(user)
                                        .build();
                        this.refreshTokenService.save(refreshTokenEntity);
                }

                LogInResponseDTO logInResponseDTO = LogInResponseDTO.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();

                return logInResponseDTO;
        }

        @Override
        public SuccessResponse<Void> logOut(Optional<String> optionalRefreshToken) {
                Jwt accessTokenJwtObject = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String userEmail = accessTokenJwtObject.getSubject();
                User user = this.userService.findByEmail(userEmail).get();

                BlackListedAccessToken blackListedAccessToken = BlackListedAccessToken.builder()
                                .jti(accessTokenJwtObject.getId())
                                .expiresAt(accessTokenJwtObject.getExpiresAt())
                                .user(user)
                                .build();
                this.blackListedAccessTokenService.save(blackListedAccessToken);

                if (optionalRefreshToken.isPresent()) {
                        this.refreshTokenService.findByTokenValueAndUserId(optionalRefreshToken.get(), user.getId())
                                        .ifPresentOrElse(
                                                        refreshTokenEntity -> this.refreshTokenService
                                                                        .delete(refreshTokenEntity),
                                                        () -> {
                                                                Set<RefreshToken> refreshTokens = user
                                                                                .getRefreshTokens();
                                                                this.refreshTokenService.deleteAll(refreshTokens);
                                                        });
                } else {
                        Set<RefreshToken> refreshTokens = user.getRefreshTokens();
                        this.refreshTokenService.deleteAll(refreshTokens);
                }

                SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Đăng xuất thành công!")
                                .build();

                return successResponse;
        }

        @Override
        public SuccessResponse<RefreshTokenResponseDTO> refreshToken(Optional<String> optionalRefreshToken) {
                try {
                        String oldRefreshTokenValue = optionalRefreshToken
                                        .orElseThrow(() -> new RefreshTokenException("Refresh token không hợp lệ!"));
                        Jwt refreshTokenJwtObject = this.refreshTokenJwtDecoder.decode(oldRefreshTokenValue);

                        String username = refreshTokenJwtObject.getSubject();
                        User user = this.userService.findByEmail(username).get();
                        RefreshToken refreshTokenEntity = user.getRefreshTokens().stream()
                                        .filter(refreshTokenObject -> oldRefreshTokenValue
                                                        .equals(refreshTokenObject.getTokenValue()))
                                        .findFirst()
                                        .orElseThrow(() -> new RefreshTokenException("Refresh token không hợp lệ!"));

                        String newAccessTokenValue = this.jwtService.createJwt(username,
                                        this.accessTokenValidityDuration, TokenTypeEnum.ACCESS.getName());

                        long remainingValidSeconds = Duration
                                        .between(Instant.now(), refreshTokenJwtObject.getExpiresAt()).getSeconds();
                        String newRefreshTokenValue = this.jwtService.createJwt(username, remainingValidSeconds,
                                        TokenTypeEnum.REFRESH.getName());

                        Jwt newRefreshTokenJwtObject = this.refreshTokenJwtDecoder.decode(newRefreshTokenValue);
                        refreshTokenEntity.setTokenValue(newRefreshTokenValue);
                        refreshTokenEntity.setJti(newRefreshTokenJwtObject.getId());
                        refreshTokenEntity.setExpiresAt(Instant.now().plusSeconds(remainingValidSeconds));
                        refreshTokenEntity.setUser(user);
                        this.refreshTokenService.save(refreshTokenEntity);

                        RefreshTokenResponseDTO refreshTokenResponseDTO = RefreshTokenResponseDTO.builder()
                                        .accessToken(newAccessTokenValue)
                                        .refreshToken(newRefreshTokenValue)
                                        .build();

                        SuccessResponse<RefreshTokenResponseDTO> successResponse = SuccessResponse
                                        .<RefreshTokenResponseDTO>builder()
                                        .status(HttpStatus.OK.value())
                                        .message("Refresh token thành công!")
                                        .data(refreshTokenResponseDTO)
                                        .build();

                        return successResponse;
                } catch (JwtException exception) {
                        throw new RefreshTokenException("Refresh token không hợp lệ!");
                }
        }

}
