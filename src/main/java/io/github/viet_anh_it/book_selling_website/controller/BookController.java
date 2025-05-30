package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.viet_anh_it.book_selling_website.dto.BookDTO;
import io.github.viet_anh_it.book_selling_website.dto.BookPriceRangeDTO;
import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Book_;
import io.github.viet_anh_it.book_selling_website.service.BookService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

    static String PROPERTY = Book_.NAME;
    static Direction DIRECTION = Direction.ASC;
    static Sort SORT = Sort.by(DIRECTION, PROPERTY);
    static int PAGE = 0;
    static int SIZE = 6;
    static Pageable PAGEABLE = PageRequest.of(PAGE, SIZE, SORT);

    @Qualifier(value = "accessTokenJwtDecoder")
    JwtDecoder accessTokenJwtDecoder;

    BookService bookService;
    JwtAuthenticationConverter jwtAuthenticationConverter;

    @GetMapping("/books")
    public String getBooksPage(Model model,
            @CookieValue(name = "access_token", defaultValue = "") String accessToken,
            @RequestParam(name = "price") Optional<BookPriceRangeDTO> optPriceParam) {
        SuccessResponse<List<BookDTO>> successResponse = this.bookService.getAllBooks(PAGEABLE, optPriceParam);
        List<BookDTO> books = successResponse.getData();
        PaginationMetadataDTO paginationMetadata = successResponse.getPaginationMetadata();
        model.addAttribute("books", books);
        model.addAttribute("paginationMetadata", paginationMetadata);

        try {
            Jwt jwt = this.accessTokenJwtDecoder.decode(accessToken);
            Authentication authentication = this.jwtAuthenticationConverter.convert(jwt);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        } catch (Exception e) {
            // return "500InternalServerError";
        }

        return "books";
    }

    @GetMapping("/books/{bookId}")
    public String getSingleBookDetailPage(Model model,
            @PathVariable(name = "bookId") String bookId,
            @CookieValue(name = "access_token", defaultValue = "") String accessToken) {
        if (bookId.matches("^\\d+$")) {
            Long id = Long.valueOf(bookId);
            Optional<BookDTO> optBookDto = this.bookService.fetchSingleBookById(id);
            if (optBookDto.isPresent()) {
                BookDTO bookDto = optBookDto.get();
                model.addAttribute("singleBook", bookDto);

                try {
                    Jwt jwt = this.accessTokenJwtDecoder.decode(accessToken);
                    Authentication authentication = this.jwtAuthenticationConverter.convert(jwt);
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                } catch (Exception e) {
                    // return "500InternalServerError";
                }

                return "singleBookDetail";
            } else {
                return "404NotFound";
            }
        } else {
            return "400BadRequest";
        }
    }

    @Secured({ "ROLE_MANAGER" })
    @GetMapping("/manager/books")
    public String getManageBooksPage(Model model) {
        SuccessResponse<List<BookDTO>> successResponse = this.bookService.getAllBooks(PAGEABLE, Optional.empty());
        List<BookDTO> books = successResponse.getData();
        PaginationMetadataDTO paginationMetadata = successResponse.getPaginationMetadata();
        model.addAttribute("books", books);
        model.addAttribute("paginationMetadata", paginationMetadata);
        return "manageBooks";
    }
}
