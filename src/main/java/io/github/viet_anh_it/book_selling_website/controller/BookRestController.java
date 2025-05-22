package io.github.viet_anh_it.book_selling_website.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.viet_anh_it.book_selling_website.dto.BookDTO;
import io.github.viet_anh_it.book_selling_website.dto.BookPriceRangeDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Book_;
import io.github.viet_anh_it.book_selling_website.service.BookService;
import io.github.viet_anh_it.book_selling_website.validator.annotation.ValidFile;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.Create;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.ValidationOrder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookRestController {

        BookService bookService;

        @Secured({ "ROLE_MANAGER" })
        @PostMapping("/books/create")
        public ResponseEntity<SuccessResponse<Void>> createBook(
                        @RequestPart(name = "json", required = true) @Validated({ Create.class,
                                        ValidationOrder.class }) BookDTO bookDTO,
                        @RequestPart(name = "bookCoverImage", required = false) @ValidFile MultipartFile bookCoverImage)
                        throws IOException {
                this.bookService.createBook(bookDTO, bookCoverImage);

                SuccessResponse<Void> successResponse = SuccessResponse
                                .<Void>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Thêm sách mới thành công!")
                                .build();

                return ResponseEntity
                                .status(HttpStatus.CREATED.value())
                                .body(successResponse);
        }

        @GetMapping("/books")
        public ResponseEntity<SuccessResponse<List<BookDTO>>> getAllBooks(
                        @PageableDefault(page = 0, size = 6, sort = Book_.NAME, direction = Sort.Direction.ASC) Pageable pageable,
                        @RequestParam(name = "price") Optional<BookPriceRangeDTO> optPriceParam) {
                return ResponseEntity
                                .status(HttpStatus.OK.value())
                                .body(this.bookService.getAllBooks(pageable, optPriceParam));
        }
}
