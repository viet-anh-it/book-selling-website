package io.github.viet_anh_it.book_selling_website.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.viet_anh_it.book_selling_website.converter.PriceQueryParamConverter;

@EnableAsync
@Configuration
@EnableScheduling
@EnableJpaAuditing
public class WebMvcConfigurerImpl implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry
                .addResourceHandler("/book-covers/**")
                .addResourceLocations("file:C:/book-selling-website/uploads/book-covers/");
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
        registry.addResourceHandler("/assets/css/**")
                .addResourceLocations("classpath:/static/assets/css/", "classpath:/static/assets/css/webfonts/");
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        registry.addConverter(new PriceQueryParamConverter());
    }

}
