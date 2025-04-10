package io.github.viet_anh_it.book_selling_website.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import io.github.viet_anh_it.book_selling_website.dto.request.LogInRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.request.SignUpRequestDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.LogInResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.RefreshTokenResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SignUpResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.event.SignUpEvent;
import io.github.viet_anh_it.book_selling_website.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

        @NonFinal
        @Value("${jwt.secret-key}")
        String jwtSecretKey;

        @NonFinal
        @Value("${jwt.access-token.validity-duration}")
        long accessTokenValidityDuration;

        @NonFinal
        @Value("${jwt.refresh-token.validity-duration}")
        long refreshTokenValidityDuration;

        AuthenticationService authenticationService;
        ApplicationEventPublisher applicationEventPublisher;

        @Qualifier("accessTokenJwtDecoder")
        JwtDecoder accessTokenJwtDecoder;

        @Qualifier("refreshTokenJwtDecoder")
        JwtDecoder refreshTokenJwtDecoder;

        @PostMapping("/sign-up")
        public ResponseEntity<SuccessResponse<SignUpResponseDTO>> signUp(
                        @Valid @RequestBody SignUpRequestDTO signUpRequestDTO, WebRequest webRequest) {

                SignUpResponseDTO signUpResponseDTO = this.authenticationService.signUp(signUpRequestDTO);
                this.applicationEventPublisher.publishEvent(
                                new SignUpEvent(signUpResponseDTO.getEmail(), webRequest.getContextPath()));

                SuccessResponse<SignUpResponseDTO> successResponse = SuccessResponse
                                .<SignUpResponseDTO>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Đăng ký thành công! Kiểm tra email để kích hoạt tài khoản của bạn!")
                                .data(signUpResponseDTO)
                                .build();

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(successResponse);
        }

        @PostMapping("/log-in")
        public ResponseEntity<SuccessResponse<Void>> logIn(
                        @Valid @RequestBody LogInRequestDTO logInRequestDTO,
                        @CookieValue(name = "refresh_token", required = false) Optional<String> optionalRefreshToken) {
                LogInResponseDTO logInResponseDTO = this.authenticationService
                                .logIn(logInRequestDTO, optionalRefreshToken);

                ResponseCookie accessTokenCookie = ResponseCookie
                                .from("access_token", logInResponseDTO.getAccessToken())
                                .httpOnly(true)
                                .path("/")
                                .maxAge(this.accessTokenValidityDuration)
                                .build();

                ResponseCookie refreshTokenCookie = ResponseCookie
                                .from("refresh_token", logInResponseDTO.getRefreshToken())
                                .httpOnly(true)
                                .path("/")
                                .maxAge(this.refreshTokenValidityDuration)
                                .build();

                SuccessResponse<Void> successResponse = SuccessResponse
                                .<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Đăng nhập thành công!")
                                .build();

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .body(successResponse);
        }

        @SuppressWarnings("null")
        @DeleteMapping("log-out")
        public ResponseEntity<SuccessResponse<Void>> logOut(
                        @CookieValue(name = "refresh_token", required = false) Optional<String> optionalRefreshToken) {
                this.authenticationService.logOut(optionalRefreshToken);

                ResponseCookie accessTokenCookie = ResponseCookie
                                .from("access_token", null)
                                .httpOnly(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                ResponseCookie refreshTokenCookie = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Đăng xuất thành công!")
                                .build();

                return ResponseEntity
                                .status(HttpStatus.OK.value())
                                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .body(successResponse);
        }

        @PutMapping("/refresh-token")
        public ResponseEntity<SuccessResponse<Void>> refreshToken(
                        @CookieValue(name = "refresh_token", required = false) Optional<String> optionalRefreshToken) {
                RefreshTokenResponseDTO refreshTokenResponseDTO = this.authenticationService
                                .refreshToken(optionalRefreshToken);

                ResponseCookie accessTokenCookie = ResponseCookie
                                .from("access_token", refreshTokenResponseDTO.getAccessToken().getTokenValue())
                                .httpOnly(true)
                                .path("/")
                                .maxAge(this.accessTokenValidityDuration)
                                .build();

                ResponseCookie refreshTokenCookie = ResponseCookie
                                .from("refresh_token", refreshTokenResponseDTO.getRefreshToken().getTokenValue())
                                .httpOnly(true)
                                .path("/")
                                .maxAge(Duration.between(Instant.now(),
                                                refreshTokenResponseDTO.getRefreshToken().getExpiresAt()).getSeconds())
                                .build();

                SuccessResponse<Void> successResponse = SuccessResponse
                                .<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Refresh token thành công!")
                                .build();

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .body(successResponse);
        }

        @SuppressWarnings("null")
        @DeleteMapping("/revoke-refresh-token")
        public ResponseEntity<SuccessResponse<Void>> revokeRefreshToken(
                        @CookieValue(name = "refresh_token", required = false) Optional<String> optionalRefreshToken) {
                this.authenticationService.revokeRefreshToken(optionalRefreshToken);

                ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", null)
                                .httpOnly(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                SuccessResponse<Void> successResponse = SuccessResponse
                                .<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Thu hồi refresh token thành công!")
                                .build();

                return ResponseEntity
                                .status(HttpStatus.OK.value())
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .body(successResponse);
        }

        @GetMapping("/confirm-registration")
        public ResponseEntity<SuccessResponse<Void>> confirmAccountRegistration(
                        @RequestParam(name = "accountRegistrationVerificationTokenString") Optional<String> optionalAccountRegistrationVerificationTokenString) {
                this.authenticationService
                                .confirmAccountRegistration(optionalAccountRegistrationVerificationTokenString);

                SuccessResponse<Void> successResponse = SuccessResponse
                                .<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Xác nhận đăng ký thành công!")
                                .build();

                return ResponseEntity
                                .status(HttpStatus.OK.value())
                                .body(successResponse);
        }
}
