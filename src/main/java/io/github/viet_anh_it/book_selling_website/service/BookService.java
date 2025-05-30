package io.github.viet_anh_it.book_selling_website.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import io.github.viet_anh_it.book_selling_website.dto.BookDTO;
import io.github.viet_anh_it.book_selling_website.dto.BookPriceRangeDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;

public interface BookService {

    void createBook(BookDTO bookDTO, MultipartFile bookCoverImage) throws IOException;

    SuccessResponse<List<BookDTO>> getAllBooks(Pageable pageable, Optional<BookPriceRangeDTO> optPriceParam);

    Optional<BookDTO> fetchSingleBookById(Long bookId);

    SuccessResponse<BookDTO> updateBookById(long bookId, BookDTO bookDto, MultipartFile bookCoverImage) throws IOException;

    void deleteBookById(long bookId);

    int getStockQuantityByBookId(long bookId);
}
