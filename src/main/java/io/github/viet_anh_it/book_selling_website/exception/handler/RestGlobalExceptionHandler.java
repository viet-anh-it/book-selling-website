package io.github.viet_anh_it.book_selling_website.exception.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.github.viet_anh_it.book_selling_website.dto.response.FailureResponse;
import io.github.viet_anh_it.book_selling_website.enums.ErrorTypeEnum;
import io.github.viet_anh_it.book_selling_website.exception.EmailException;
import io.github.viet_anh_it.book_selling_website.exception.RefreshTokenException;
import io.github.viet_anh_it.book_selling_website.exception.RoleNotFoundException;

@RestControllerAdvice(annotations = RestController.class)
public class RestGlobalExceptionHandler {

        @ExceptionHandler({ EmailException.class })
        public ResponseEntity<FailureResponse<Map<String, String>>> handleEmailException(EmailException exception) {
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("email", exception.getMessage());

                FailureResponse<Map<String, String>> failureResponse = FailureResponse.<Map<String, String>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .type(ErrorTypeEnum.VALIDATION)
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
                                .type(ErrorTypeEnum.VALIDATION)
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
                        failureResponse.setType(ErrorTypeEnum.VALIDATION);

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
                                .type(ErrorTypeEnum.SERVER_ERROR)
                                .detail(exception.getMessage())
                                .build();

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .body(failureResponse);
        }

        @ExceptionHandler({ MailException.class })
        public ResponseEntity<FailureResponse<String>> handleMailException(MailException exception) {
                FailureResponse<String> failureResponse = FailureResponse
                                .<String>builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                .type(ErrorTypeEnum.SERVER_ERROR)
                                .detail("Có lỗi xảy ra trong quá trình gửi email!")
                                .build();

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .body(failureResponse);
        }

        @ExceptionHandler({ HandlerMethodValidationException.class })
        public ResponseEntity<FailureResponse<Map<String, String>>> handleHandlerMethodValidationException(
                        HandlerMethodValidationException exception) {
                Map<String, String> errors = exception.getParameterValidationResults().stream()
                                .flatMap(paramResult -> paramResult.getResolvableErrors().stream()
                                                .map(error -> Map.entry(
                                                                paramResult.getMethodParameter().getParameterName(),
                                                                error.getDefaultMessage())))
                                .collect(Collectors.toMap(
                                                entry -> entry.getKey(),
                                                entry -> entry.getValue(),
                                                (existing, replacement) -> existing));

                FailureResponse<Map<String, String>> failureResponse = FailureResponse
                                .<Map<String, String>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .type(ErrorTypeEnum.VALIDATION)
                                .detail(errors)
                                .build();

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST.value())
                                .body(failureResponse);
        }

        @ExceptionHandler({ PropertyReferenceException.class })
        public ResponseEntity<FailureResponse<String>> handlePropertyReferenceException(
                        PropertyReferenceException exception) {
                FailureResponse<String> failureResponse = FailureResponse.<String>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .detail(exception.getMessage())
                                .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(failureResponse);
        }

        @ExceptionHandler({ HttpMessageNotReadableException.class })
        public ResponseEntity<FailureResponse<Map<String, String>>> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex) {
                FailureResponse<Map<String, String>> failureResponse = FailureResponse.<Map<String, String>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .type(ErrorTypeEnum.DESERIALIZATION)
                                .build();

                Throwable cause = ex.getMostSpecificCause();
                String fieldName = null;
                String errorMessage = null;
                Map<String, String> errorDetails = new HashMap<>();

                if (cause instanceof JsonParseException) {
                        JsonParseException jpe = (JsonParseException) cause;
                        JsonParser parser = jpe.getProcessor();
                        try {
                                fieldName = parser.currentName();
                                errorMessage = jpe.getOriginalMessage();
                        } catch (IOException e) {
                                // TODO: ghi log
                        }
                } else if (cause instanceof JsonMappingException) {
                        JsonMappingException jme = (JsonMappingException) cause;
                        List<JsonMappingException.Reference> path = jme.getPath();
                        if (!path.isEmpty() && path.getFirst().getFieldName() != null) {
                                fieldName = path.getFirst().getFieldName();
                                errorMessage = jme.getOriginalMessage();
                        }
                }

                errorDetails.put(fieldName, errorMessage);
                failureResponse.setDetail(errorDetails);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(failureResponse);
        }
}
