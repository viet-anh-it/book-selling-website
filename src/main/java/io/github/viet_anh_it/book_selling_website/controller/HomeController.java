package io.github.viet_anh_it.book_selling_website.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.viet_anh_it.book_selling_website.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeController {

    @NonFinal
    @Value("${jwt.access-token.validity-duration}")
    long accessTokenValidityDuration;

    @NonFinal
    @Value("${jwt.refresh-token.validity-duration}")
    long refreshTokenValidityDuration;

    @Qualifier("accessTokenJwtDecoder")
    JwtDecoder accessTokenJwtDecoder;

    @Qualifier("refreshTokenJwtDecoder")
    JwtDecoder refreshTokenJwtDecoder;

    AuthService authService;

    @GetMapping("/home")
    public String getHomePage(
            @CookieValue(name = "access_token", required = false) Optional<String> optionalAccessToken,
            @CookieValue(name = "refresh_token", required = false) Optional<String> optionalRefreshToken,
            HttpServletResponse httpServletResponse, Model model) {
        // if (optionalAccessToken.isPresent()) {
        // String accessToken = optionalAccessToken.get();
        // try {
        // Jwt accessTokenJwtObject = this.accessTokenJwtDecoder.decode(accessToken); //
        // nếu access token expired
        // String username = accessTokenJwtObject.getSubject();
        // model.addAttribute("username", username);
        // return "home";
        // } catch (JwtException accessTokenJwtException) {
        // try {
        // SuccessResponse<RefreshTokenResponseDTO> successResponse = this.authService
        // .refreshToken(optionalRefreshToken); // throw RefreshTokenException

        // String newAccessToken = successResponse.getData().getAccessToken();
        // String newRefreshToken = successResponse.getData().getRefreshToken();

        // String oldRefreshToken = optionalRefreshToken.get();
        // Jwt refreshTokenJwtObject =
        // this.refreshTokenJwtDecoder.decode(oldRefreshToken);
        // long remainingValidSeconds = Duration
        // .between(Instant.now(), refreshTokenJwtObject.getExpiresAt()).getSeconds();

        // Cookie accessTokenCookie = new Cookie("access_token", newAccessToken);
        // accessTokenCookie.setHttpOnly(true);
        // accessTokenCookie.setPath("/");
        // accessTokenCookie.setMaxAge((int) this.accessTokenValidityDuration);

        // Cookie refreshTokenCookie = new Cookie("refresh_token", newRefreshToken);
        // refreshTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setPath("/");
        // refreshTokenCookie.setMaxAge((int) remainingValidSeconds);

        // httpServletResponse.addCookie(accessTokenCookie);
        // httpServletResponse.addCookie(refreshTokenCookie);

        // return "redirect:/home";
        // } catch (JwtException refreshTokenJwtException) {
        // return "home";
        // // return "redirect:/log-in";
        // }
        // }
        // }
        return "home";
    }

    @ResponseBody
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, client!";
    }
}
