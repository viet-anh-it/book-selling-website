package io.github.viet_anh_it.book_selling_website.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.viet_anh_it.book_selling_website.dto.response.FailureResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler({ EmailAlreadyExistedException.class })
        public ResponseEntity<FailureResponse<Map<String, String>>> handleEmailAlreadyExistedException(
                        EmailAlreadyExistedException exception) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("email", exception.getMessage());

                FailureResponse<Map<String, String>> failureResponse = FailureResponse.<Map<String, String>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .detail(errorDetails)
                                .build();

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(failureResponse);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<FailureResponse<Map<String, String>>> handleValidationException(
                        MethodArgumentNotValidException exception) {
                Map<String, String> errorDetails = new HashMap<>();
                exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
                        String fieldName = fieldError.getField();
                        String errorMessage = fieldError.getDefaultMessage();
                        errorDetails.put(fieldName, errorMessage);
                });

                FailureResponse<Map<String, String>> failureResponse = FailureResponse.<Map<String, String>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .detail(errorDetails)
                                .build();

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST.value())
                                .body(failureResponse);
        }

        @SuppressWarnings("null")
        @ExceptionHandler(JwtException.class)
        public ResponseEntity<FailureResponse<String>> handleJwtException(JwtException exception) {
                FailureResponse<String> failureResponse = FailureResponse.<String>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .detail(exception.getMessage())
                                .build();

                if (exception instanceof RefreshTokenException) {
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

                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST.value())
                                        .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                                        .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                        .body(failureResponse);
                }

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST.value())
                                .body(failureResponse);
        }

        @ExceptionHandler(RoleNotFoundException.class)
        public ResponseEntity<FailureResponse<String>> handleRoleNotFoundException(RoleNotFoundException exception) {
                FailureResponse<String> failureResponse = FailureResponse.<String>builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                .detail(exception.getMessage())
                                .build();

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .body(failureResponse);
        }
}
