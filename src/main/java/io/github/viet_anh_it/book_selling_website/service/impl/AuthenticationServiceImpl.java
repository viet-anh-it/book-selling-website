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
import io.github.viet_anh_it.book_selling_website.dto.request.ResetPasswordRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.request.SendEmailRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.request.SignUpRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.LogInResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.RefreshTokenResponseDTO;
import io.github.viet_anh_it.book_selling_website.enums.RoleEnum;
import io.github.viet_anh_it.book_selling_website.enums.TokenTypeEnum;
import io.github.viet_anh_it.book_selling_website.exception.AccountStatusException;
import io.github.viet_anh_it.book_selling_website.exception.ActivateAccountTokenException;
import io.github.viet_anh_it.book_selling_website.exception.EmailAlreadyExistedException;
import io.github.viet_anh_it.book_selling_website.exception.ForgotPasswordTokenException;
import io.github.viet_anh_it.book_selling_website.exception.RefreshTokenException;
import io.github.viet_anh_it.book_selling_website.exception.RoleNotFoundException;
import io.github.viet_anh_it.book_selling_website.model.Address;
import io.github.viet_anh_it.book_selling_website.model.BlackListedAccessToken;
import io.github.viet_anh_it.book_selling_website.model.Cart;
import io.github.viet_anh_it.book_selling_website.model.RefreshToken;
import io.github.viet_anh_it.book_selling_website.model.Role;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.model.VerificationToken;
import io.github.viet_anh_it.book_selling_website.service.AuthenticationService;
import io.github.viet_anh_it.book_selling_website.service.BlackListedAccessTokenService;
import io.github.viet_anh_it.book_selling_website.service.EmailService;
import io.github.viet_anh_it.book_selling_website.service.JwtService;
import io.github.viet_anh_it.book_selling_website.service.RefreshTokenService;
import io.github.viet_anh_it.book_selling_website.service.RoleService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import io.github.viet_anh_it.book_selling_website.service.VerificationTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

        static String ROLE_PREFIX = "ROLE_";
        static String ROLE_UNKNOWN = ROLE_PREFIX + "UNKNOWN";

        @NonFinal
        @Value("${jwt.secret-key}")
        String jwtSecretKey;

        @Qualifier("accessTokenJwtDecoder")
        JwtDecoder accessTokenJwtDecoder;

        @Qualifier("refreshTokenJwtDecoder")
        JwtDecoder refreshTokenJwtDecoder;

        @NonFinal
        @Value("${jwt.access-token.validity-duration}")
        long accessTokenValidityDuration;

        @NonFinal
        @Value("${jwt.refresh-token.validity-duration}")
        long refreshTokenValidityDuration;

        JwtService jwtService;
        UserService userService;
        RoleService roleService;
        EmailService emailService;
        PasswordEncoder passwordEncoder;
        RefreshTokenService refreshTokenService;
        AuthenticationManager authenticationManager;
        VerificationTokenService verificationTokenService;
        BlackListedAccessTokenService blackListedAccessTokenService;

        @Override
        public void signUp(SignUpRequestDTO signUpRequestDTO) {
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
                                .active(false)
                                .build();
                user.setCart(new Cart());
                user.setAddress(new Address());
                user = this.userService.save(user);

                this.emailService.sendAccountActivationEmailTemplate(user);
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
                                this.accessTokenValidityDuration, TokenTypeEnum.ACCESS.name().toLowerCase());
                Jwt newRefreshTokenJwtObject = this.jwtService.createJwt(authentication.getName(),
                                this.refreshTokenValidityDuration, TokenTypeEnum.REFRESH.name().toLowerCase());

                String userEmail = authentication.getName();
                User user = this.userService.findByEmail(userEmail).get();
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

                String role = authentication.getAuthorities().stream()
                                .filter(auth -> auth.getAuthority().startsWith(ROLE_PREFIX))
                                .map(auth -> auth.getAuthority())
                                .findFirst()
                                .orElse(ROLE_UNKNOWN);

                LogInResponseDTO logInResponseDTO = LogInResponseDTO
                                .builder()
                                .accessToken(newAccessTokenJwtObject.getTokenValue())
                                .refreshToken(newRefreshTokenJwtObject.getTokenValue())
                                .role(role)
                                .build();

                return logInResponseDTO;
        }

        @Override
        public void logOut(Optional<String> optionalOldRefreshTokenString) {
                Jwt accessTokenJwtObject = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String userEmail = accessTokenJwtObject.getSubject();
                User user = this.userService.findByEmail(userEmail).get();

                BlackListedAccessToken blackListedAccessToken = BlackListedAccessToken
                                .builder()
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
                                        this.accessTokenValidityDuration, TokenTypeEnum.ACCESS.name().toLowerCase());
                        long remainingValidSeconds = Duration
                                        .between(Instant.now(), oldRefreshTokenJwtObject.getExpiresAt()).getSeconds();
                        Jwt newRefreshTokenJwtObject = this.jwtService.createJwt(username, remainingValidSeconds,
                                        TokenTypeEnum.REFRESH.name().toLowerCase());

                        User user = this.userService.findByEmail(username).get();
                        RefreshToken refreshTokenEntity = this.refreshTokenService.findByTokenValueAndUserIdAndJti(
                                        oldRefreshTokenString, user.getId(), oldRefreshTokenJwtObject.getId()).get();
                        refreshTokenEntity.setTokenValue(newRefreshTokenJwtObject.getTokenValue());
                        refreshTokenEntity.setJti(newRefreshTokenJwtObject.getId());
                        refreshTokenEntity.setExpiresAt(newRefreshTokenJwtObject.getExpiresAt());
                        refreshTokenEntity.setUser(user);
                        this.refreshTokenService.save(refreshTokenEntity);

                        RefreshTokenResponseDTO refreshTokenResponseDTO = RefreshTokenResponseDTO
                                        .builder()
                                        .accessToken(newAccessTokenJwtObject)
                                        .refreshToken(newRefreshTokenJwtObject)
                                        .build();

                        return refreshTokenResponseDTO;
                } catch (JwtException exception) {
                        throw new RefreshTokenException("Refresh token không hợp lệ!");
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

        @Override
        public void activateAccount(Optional<String> optionalAccountActivationTokenString) {
                if (optionalAccountActivationTokenString.isPresent()) {
                        String accountActivationTokenString = optionalAccountActivationTokenString.get();
                        VerificationToken verificationTokenEntity = this.verificationTokenService
                                        .findByTokenValue(accountActivationTokenString)
                                        .orElseThrow(() -> new ActivateAccountTokenException(
                                                        "Có lỗi xảy ra! Nhấn Gửi lại email để thử lại!"));
                        if (verificationTokenEntity.getUser() == null) {
                                throw new ActivateAccountTokenException(
                                                "Có lỗi xảy ra! Nhấn Gửi lại email để thử lại!");
                        } else if (!TokenTypeEnum.ACTIVATE_ACCOUNT.name()
                                        .equals(verificationTokenEntity.getType().name())) {
                                throw new ActivateAccountTokenException(
                                                "Có lỗi xảy ra! Nhấn Gửi lại email để thử lại!");
                        } else if (Instant.now().isAfter(verificationTokenEntity.getExpiresAt())) {
                                throw new ActivateAccountTokenException(
                                                "Có lỗi xảy ra! Nhấn Gửi lại email để thử lại!");
                        }

                        User user = verificationTokenEntity.getUser();
                        user.setActive(true);
                        user.setVerificationToken(null);
                        this.userService.save(user);
                        this.verificationTokenService.delete(verificationTokenEntity);
                } else {
                        throw new ActivateAccountTokenException(
                                        "Có lỗi xảy ra! Nhấn Gửi lại email để thử lại!");
                }
        }

        @Override
        public void sendActivateAccountEmail(SendEmailRequestDTO sendEmailRequestDTO) {
                User user = this.userService.findByEmail(sendEmailRequestDTO.getEmail()).get();
                if (user.isActive()) {
                        throw new AccountStatusException("Tài khoản đã kích hoạt!");
                }

                Optional.ofNullable(user.getVerificationToken())
                                .ifPresent(verificationTokenEntity -> {
                                        user.setVerificationToken(null);
                                        this.verificationTokenService.delete(verificationTokenEntity);
                                });

                this.emailService.sendAccountActivationEmailTemplate(user);
        }

        @Override
        public void sendForgotPasswordEmail(SendEmailRequestDTO sendEmailRequestDTO) {
                User user = this.userService.findByEmail(sendEmailRequestDTO.getEmail()).get();
                if (!user.isActive()) {
                        throw new AccountStatusException("Tài khoản chưa kích hoạt!");
                }
                Optional.ofNullable(user.getVerificationToken())
                                .ifPresent(token -> {
                                        user.setVerificationToken(null);
                                        this.userService.save(user);
                                        this.verificationTokenService.delete(token);
                                });
                this.emailService.sendForgotPasswordEmailTemplate(user);
        }

        @Override
        public String validateForgotPasswordToken(Optional<String> optionalToken) {
                if (optionalToken.isPresent()) {
                        String token = optionalToken.get();
                        VerificationToken tokenEntity = this.verificationTokenService
                                        .findByTokenValue(token)
                                        .orElseThrow(() -> new ForgotPasswordTokenException(
                                                        "Có lỗi xảy ra! Nhấn Quên mật khẩu để thử lại!"));
                        if (tokenEntity.getUser() == null) {
                                throw new ForgotPasswordTokenException(
                                                "Có lỗi xảy ra! Nhấn Quên mật khẩu để thử lại!");
                        } else if (!TokenTypeEnum.FORGOT_PASSWORD.name()
                                        .equals(tokenEntity.getType().name())) {
                                throw new ForgotPasswordTokenException(
                                                "Có lỗi xảy ra! Nhấn Quên mật khẩu để thử lại!");
                        } else if (Instant.now().isAfter(tokenEntity.getExpiresAt())) {
                                throw new ForgotPasswordTokenException(
                                                "Có lỗi xảy ra! Nhấn Quên mật khẩu để thử lại!");
                        }
                        return tokenEntity.getTokenValue();
                } else {
                        throw new ForgotPasswordTokenException(
                                        "Có lỗi xảy ra! Nhấn Quên mật khẩu để thử lại!");
                }
        }

        @Override
        public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
                User user = this.verificationTokenService
                                .findByTokenValue(resetPasswordRequestDTO.getToken())
                                .get()
                                .getUser();
                String hashedPassword = this.passwordEncoder.encode(resetPasswordRequestDTO.getPassword());
                user.setPassword(hashedPassword);
                this.userService.save(user);
        }

}
