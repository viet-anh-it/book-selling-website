package io.github.viet_anh_it.book_selling_website.custom;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.proc.BadJWTException;

import io.github.viet_anh_it.book_selling_website.dto.response.FailureResponse;
import io.github.viet_anh_it.book_selling_website.enums.ErrorTypeEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BearerTokenAuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint = new BearerTokenAuthenticationEntryPoint();
        bearerTokenAuthenticationEntryPoint.commence(request, response, authException);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        FailureResponse<String> failureResponse = new FailureResponse<>();
        failureResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        failureResponse.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());

        if (authException instanceof InvalidBearerTokenException) {
            InvalidBearerTokenException invalidBearerTokenException = (InvalidBearerTokenException) authException;
            Throwable cause = invalidBearerTokenException.getCause();
            if (cause instanceof JwtValidationException) {
                JwtValidationException jwtValidationException = (JwtValidationException) cause;
                String validationErrorString = jwtValidationException.getMessage().toLowerCase();
                if (validationErrorString.contains("expired") && jwtValidationException.getErrors().size() == 1) {
                    response.addHeader("X-Error-Type", ErrorTypeEnum.TOKEN_EXPIRED.name());
                    failureResponse.setType(ErrorTypeEnum.TOKEN_EXPIRED);
                    failureResponse.setDetail("Access token hết hạn!");
                } else {
                    response.addHeader("X-Error-Type", ErrorTypeEnum.INVALID_TOKEN.name());
                    failureResponse.setType(ErrorTypeEnum.INVALID_TOKEN);
                    failureResponse.setDetail("Access token không hợp lệ!");
                }
            } else if (cause instanceof BadJwtException) {
                BadJwtException badJwtException = (BadJwtException) cause;
                Throwable badJwtExceptionCause = badJwtException.getCause();
                if (badJwtExceptionCause instanceof BadJWTException) {
                    BadJWTException nimbusBadJWTException = (BadJWTException) badJwtExceptionCause;
                    String message = nimbusBadJWTException.getMessage().toLowerCase();
                    if (message.contains("expired")) {
                        response.addHeader("X-Error-Type", ErrorTypeEnum.TOKEN_EXPIRED.name());
                        failureResponse.setType(ErrorTypeEnum.TOKEN_EXPIRED);
                        failureResponse.setDetail("Access token hết hạn!");
                    } else {
                        response.addHeader("X-Error-Type", ErrorTypeEnum.INVALID_TOKEN.name());
                        failureResponse.setType(ErrorTypeEnum.INVALID_TOKEN);
                        failureResponse.setDetail("Access token không hợp lệ!");
                    }
                } else {
                    response.addHeader("X-Error-Type", ErrorTypeEnum.INVALID_TOKEN.name());
                    failureResponse.setType(ErrorTypeEnum.INVALID_TOKEN);
                    failureResponse.setDetail("Access token không hợp lệ!");
                }
            }
        } else if (authException instanceof AuthenticationServiceException) {
            response.addHeader("X-Error-Type", ErrorTypeEnum.INVALID_TOKEN.name());
            failureResponse.setType(ErrorTypeEnum.INVALID_TOKEN);
            failureResponse.setDetail("Access token không hợp lệ!");
        } else if (authException instanceof UsernameNotFoundException
                || authException instanceof BadCredentialsException) {
            response.addHeader("X-Error-Type", ErrorTypeEnum.AUTHENTICATION.name());
            failureResponse.setType(ErrorTypeEnum.AUTHENTICATION);
            failureResponse.setDetail("Thông tin xác thực không chính xác!");
        } else if (authException instanceof InsufficientAuthenticationException) {
            response.addHeader("X-Error-Type", ErrorTypeEnum.AUTHENTICATION.name());
            failureResponse.setType(ErrorTypeEnum.AUTHENTICATION);
            failureResponse.setDetail("Người dùng không xác thực!");
        } else if (authException instanceof DisabledException) {
            response.addHeader("X-Error-Type", ErrorTypeEnum.AUTHENTICATION.name());
            failureResponse.setType(ErrorTypeEnum.ACCOUNT_DISABLED);
            failureResponse.setDetail("Tài khoản chưa được kích hoạt!");
        } else if (authException instanceof LockedException) {
            response.addHeader("X-Error-Type", ErrorTypeEnum.ACCOUNT_LOCKED.name());
            failureResponse.setType(ErrorTypeEnum.AUTHENTICATION);
            failureResponse.setDetail("Tài khoản bị khóa!");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(failureResponse);

        PrintWriter printWriter = response.getWriter();
        printWriter.write(json);

        response.flushBuffer();
    }

}
