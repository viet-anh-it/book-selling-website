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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.OnCreate;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.OnUpdate;
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
                        @RequestPart(name = "json", required = true) @Validated({ OnCreate.class,
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
                        @RequestParam(name = "price") Optional<BookPriceRangeDTO> optPriceParam,
                        @RequestParam(name = "category") Optional<Long> optCategoryParam,
                        @RequestParam(name = "rate") Optional<Integer> optRateParam,
                        @RequestParam(name = "keyword") Optional<String> optKeywordParam,
                        @RequestParam(name = "stock") Optional<Boolean> optStockParam) {
                return ResponseEntity
                                .status(HttpStatus.OK.value())
                                .body(this.bookService.getAllBooks(pageable, optPriceParam, optCategoryParam, optRateParam, optKeywordParam, optStockParam));
        }

        @Secured({ "ROLE_MANAGER", "ROLE_STAFF" })
        @GetMapping("/books/{bookId}")
        public ResponseEntity<SuccessResponse<BookDTO>> getBookById(@PathVariable(name = "bookId") long bookId) {
                Optional<BookDTO> optBookDto = this.bookService.fetchSingleBookById(bookId);
                SuccessResponse<BookDTO> successResponse = SuccessResponse.<BookDTO>builder()
                                .status(HttpStatus.OK.value())
                                .message("Lấy sách thành công!")
                                .data(optBookDto.isPresent() ? optBookDto.get() : null)
                                .build();
                return ResponseEntity
                                .status(HttpStatus.OK.value())
                                .body(successResponse);
        }

        @Secured({ "ROLE_MANAGER" })
        @PutMapping("/books/{bookId}")
        public ResponseEntity<SuccessResponse<BookDTO>> updateBookById(@PathVariable long bookId,
                        @RequestPart(name = "json") @Validated({ OnUpdate.class, ValidationOrder.class }) BookDTO bookDto,
                        @RequestPart(name = "bookCoverImage", required = false) @ValidFile MultipartFile bookCoverImage)
                        throws IOException {
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(this.bookService.updateBookById(bookId, bookDto, bookCoverImage));
        }

        @Secured({ "ROLE_MANAGER" })
        @DeleteMapping("/books/{bookId}")
        public ResponseEntity<SuccessResponse<Void>> deleteBookById(@PathVariable long bookId) {
                this.bookService.deleteBookById(bookId);
                SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Xóa sách thành công!")
                                .build();
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(successResponse);
        }

        // @Secured({ "ROLE_CUSTOMER" })
        @GetMapping("/books/{bookId}/stockquantity")
        public ResponseEntity<SuccessResponse<Integer>> getStockQuantityByBookId(@PathVariable long bookId) {
                SuccessResponse<Integer> successResponse = SuccessResponse.<Integer>builder()
                                .status(HttpStatus.OK.value())
                                .message("Lấy số lượng tồn kho thành công!")
                                .data(this.bookService.getStockQuantityByBookId(bookId))
                                .build();
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(successResponse);
        }
}
