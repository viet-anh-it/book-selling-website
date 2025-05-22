package io.github.viet_anh_it.book_selling_website.configuration;

import java.util.List;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import io.github.viet_anh_it.book_selling_website.custom.BearerTokenAccessDeniedHandlerImpl;
import io.github.viet_anh_it.book_selling_website.custom.BearerTokenAuthenticationEntryPointImpl;
import io.github.viet_anh_it.book_selling_website.custom.CookieBearerTokenResolverImpl;
import io.github.viet_anh_it.book_selling_website.custom.DelegatingBearerTokenResolverImpl;
import io.github.viet_anh_it.book_selling_website.custom.JwtGrantedAuthoritiesConverterImpl;
import io.github.viet_anh_it.book_selling_website.custom.UserDetailsServiceImpl;
import io.github.viet_anh_it.book_selling_website.enums.TokenTypeEnum;
import io.github.viet_anh_it.book_selling_website.service.BlackListedAccessTokenService;
import io.github.viet_anh_it.book_selling_website.service.PermissionService;
import io.github.viet_anh_it.book_selling_website.service.RefreshTokenService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import jakarta.servlet.DispatcherType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = false)
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration {

        @NonFinal
        String[] whitelist = { "/signUp", "/activateAccount", "/sendActivateAccountEmail", "/logIn",
                        "/sendForgotPasswordEmail", "/forgotPassword", "/resetPassword", "/refreshToken",
                        "/revokeRefreshToken", "/books/**", "/assets/**", "/book-covers/**",
                        "/error/403Forbidden", "/error/401Unauthorized", "/api/books" };

        @NonFinal
        @Value("${jwt.secret-key}")
        String jwtSecretKey;

        UserService userService;
        PermissionService permissionService;
        RefreshTokenService refreshTokenService;
        BlackListedAccessTokenService blackListedAccessTokenService;

        // private final UserService userService;
        // private final PermissionService permissionService;
        // private final RefreshTokenService refreshTokenService;
        // private final BlackListedAccessTokenService blackListedAccessTokenService;

        // public SecurityConfiguration(UserService userService,
        // BlackListedAccessTokenService blackListedAccessTokenService,
        // PermissionService permissionService, RefreshTokenService refreshTokenService)
        // {
        // this.userService = userService;
        // this.blackListedAccessTokenService = blackListedAccessTokenService;
        // this.permissionService = permissionService;
        // this.refreshTokenService = refreshTokenService;
        // }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                return new UserDetailsServiceImpl(this.userService, this.permissionService);
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
                authenticationProvider.setUserDetailsService(userDetailsService);
                authenticationProvider.setPasswordEncoder(passwordEncoder);

                return new ProviderManager(authenticationProvider);
        }

        @Bean
        public JwtEncoder jwtEncoder() {
                JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(
                                this.jwtSecretKey.getBytes());
                NimbusJwtEncoder nimbusJwtEncoder = new NimbusJwtEncoder(jwkSource);
                return nimbusJwtEncoder;
        }

        @Bean("accessTokenJwtDecoder")
        public JwtDecoder accessTokenJwtDecoder() {
                SecretKey secretKey = new SecretKeySpec(this.jwtSecretKey.getBytes(), MacAlgorithm.HS256.getName());
                NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                                .macAlgorithm(MacAlgorithm.HS256)
                                .build();
                JwtClaimValidator<String> usageClaimValidator = new JwtClaimValidator<>("usage",
                                usageClaimValue -> usageClaimValue.equalsIgnoreCase(TokenTypeEnum.ACCESS.name()));
                JwtClaimValidator<String> jtiClaimValidator = new JwtClaimValidator<>("jti",
                                jtiClaimValue -> !this.blackListedAccessTokenService.existsByJti(jtiClaimValue));
                JwtClaimValidator<String> subClaimValidator = new JwtClaimValidator<>("sub",
                                subClaimValue -> this.userService.existsByEmail(subClaimValue));
                OAuth2TokenValidator<Jwt> oAuth2TokenValidator = JwtValidators
                                .createDefaultWithValidators(List.of(usageClaimValidator,
                                                jtiClaimValidator, subClaimValidator));
                nimbusJwtDecoder.setJwtValidator(oAuth2TokenValidator);
                return nimbusJwtDecoder;
        }

        @Bean("refreshTokenJwtDecoder")
        public JwtDecoder refreshTokenJwtDecoder() {
                SecretKey secretKey = new SecretKeySpec(this.jwtSecretKey.getBytes(), MacAlgorithm.HS256.getName());
                NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                                .macAlgorithm(MacAlgorithm.HS256)
                                .build();
                JwtClaimValidator<String> usageClaimValidator = new JwtClaimValidator<>("usage",
                                usageClaimValue -> usageClaimValue.equalsIgnoreCase(TokenTypeEnum.REFRESH.name()));
                JwtClaimValidator<String> jtiClaimValidator = new JwtClaimValidator<>("jti",
                                jtiClaimValue -> this.refreshTokenService.existsByJti(jtiClaimValue));
                JwtClaimValidator<String> subClaimValidator = new JwtClaimValidator<>("sub",
                                subClaimValue -> this.userService.existsByEmail(subClaimValue));
                OAuth2TokenValidator<Jwt> oAuth2TokenValidator = JwtValidators
                                .createDefaultWithValidators(
                                                List.of(usageClaimValidator, jtiClaimValidator, subClaimValidator));
                nimbusJwtDecoder.setJwtValidator(oAuth2TokenValidator);
                return nimbusJwtDecoder;
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverterImpl grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverterImpl(
                                this.userDetailsService());

                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
                return jwtAuthenticationConverter;
        }

        @Bean
        public BearerTokenResolver bearerTokenResolver() {
                BearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
                BearerTokenResolver cookieBearerTokenResolver = new CookieBearerTokenResolverImpl(this.whitelist);
                return new DelegatingBearerTokenResolverImpl(
                                Set.of(defaultBearerTokenResolver, cookieBearerTokenResolver));
        }

        @Bean
        public AuthenticationEntryPoint authenticationEntryPoint() {
                return new BearerTokenAuthenticationEntryPointImpl();
        }

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {
                return new BearerTokenAccessDeniedHandlerImpl();
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                return (web) -> web.ignoring()
                                .requestMatchers("/book-covers/**");
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(httpRequest -> httpRequest
                                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE,
                                                DispatcherType.ERROR)
                                .permitAll()
                                .requestMatchers(this.whitelist).permitAll()
                                .anyRequest().authenticated())
                                .formLogin(formLogin -> formLogin.disable())
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(sessionManagement -> sessionManagement
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .oauth2ResourceServer(
                                                oauth2ResourceServer -> oauth2ResourceServer
                                                                .jwt(jwt -> jwt.decoder(accessTokenJwtDecoder())
                                                                                .jwtAuthenticationConverter(
                                                                                                jwtAuthenticationConverter()))
                                                                .authenticationEntryPoint(authenticationEntryPoint())
                                                                .accessDeniedHandler(accessDeniedHandler())
                                                                .bearerTokenResolver(bearerTokenResolver()));
                return http.build();
        }
}
