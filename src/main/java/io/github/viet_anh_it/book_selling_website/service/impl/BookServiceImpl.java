package io.github.viet_anh_it.book_selling_website.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.github.viet_anh_it.book_selling_website.dto.BookDTO;
import io.github.viet_anh_it.book_selling_website.dto.BookPriceRangeDTO;
import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Book;
import io.github.viet_anh_it.book_selling_website.repository.BookRepository;
import io.github.viet_anh_it.book_selling_website.service.BookService;
import io.github.viet_anh_it.book_selling_website.specification.BookSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookServiceImpl implements BookService {

        @Value("${app.upload-dir.image.book-cover}")
        String bookCoverImageUploadDir;

        @NonNull
        BookRepository bookRepository;

        @PreAuthorize("hasAuthority('CREATE_BOOK')")
        @Override
        public void createBook(BookDTO bookDTO, MultipartFile bookCoverImage) throws IOException {

                @SuppressWarnings("null")
                String cleanFileName = StringUtils.cleanPath(bookCoverImage.getOriginalFilename());
                String fileExtension = StringUtils.getFilenameExtension(cleanFileName);
                String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

                Path uploadPath = Paths.get(this.bookCoverImageUploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);
                Path targetLocation = uploadPath.resolve(uniqueFileName);
                bookCoverImage.transferTo(targetLocation.toFile());

                Book book = Book
                                .builder()
                                .isbn(bookDTO.getIsbn())
                                .name(bookDTO.getName())
                                .authorName(bookDTO.getAuthor())
                                .publisherName(bookDTO.getPublisher())
                                .price(bookDTO.getPrice())
                                .stockQuantity(bookDTO.getStockQuantity())
                                .image(targetLocation.toAbsolutePath().toString())
                                .description(bookDTO.getDescription())
                                .build();

                this.bookRepository.save(book);
        }

        @Override
        public SuccessResponse<List<BookDTO>> getAllBooks(Pageable pageable,
                        Optional<BookPriceRangeDTO> optPriceParam) {
                // build specification
                Specification<Book> priceSpec = null;
                if (optPriceParam.isPresent()) {
                        BookPriceRangeDTO bookPriceRangeDTO = optPriceParam.get();
                        priceSpec = BookSpecification.hasPriceInRange(bookPriceRangeDTO.getMinPrice(),
                                        bookPriceRangeDTO.getMaxPrice());
                }
                // end build specification

                Page<Book> pagedBooks = this.bookRepository.findAll(priceSpec, pageable);
                List<BookDTO> bookDtoList = pagedBooks.getContent()
                                .stream()
                                .map(book -> BookDTO.builder()
                                                .id(book.getId())
                                                // .isbn(book.getIsbn())
                                                .name(book.getName())
                                                .author(book.getAuthorName())
                                                // .publisher(book.getPublisherName())
                                                .price(book.getPrice())
                                                .image(book.getImage()
                                                                .substring(book.getImage().indexOf("\\book-covers")))
                                                // .description(book.getDescription())
                                                .build())
                                .toList();

                PaginationMetadataDTO paginationMetadataDTO = PaginationMetadataDTO.builder()
                                .currentPosition(pagedBooks.getNumber())
                                .numberOfElementsPerPage(pagedBooks.getSize())
                                .totalNumberOfElements(pagedBooks.getTotalElements())
                                .totalNumberOfPages(pagedBooks.getTotalPages())
                                .hasNextPage(pagedBooks.hasNext())
                                .hasPreviousPage(pagedBooks.hasPrevious())
                                .nextPosition(pagedBooks.hasNext() ? pagedBooks.getNumber() + 1 : -1)
                                .previousPosition(pagedBooks.hasPrevious() ? pagedBooks.getNumber() - 1 : -1)
                                .build();

                return SuccessResponse.<List<BookDTO>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Lấy tất cả sách thành công!")
                                .data(bookDtoList)
                                .paginationMetadata(paginationMetadataDTO)
                                .build();
        }

        @Override
        public Optional<BookDTO> fetchSingleBookById(Long bookId) {
                Optional<Book> optBook = this.bookRepository.findById(bookId);
                if (optBook.isPresent()) {
                        Book book = optBook.get();
                        BookDTO bookDto = BookDTO.builder()
                                        .id(book.getId())
                                        .name(book.getName())
                                        .price(book.getPrice())
                                        .author(book.getAuthorName())
                                        .publisher(book.getPublisherName())
                                        .description(book.getDescription())
                                        .stockQuantity(book.getStockQuantity())
                                        .averageRatingPoint(book.getReviewStat().getAverageRatingPoint())
                                        .totalReviews(book.getReviewStat().getTotalReviews())
                                        .image(book.getImage().substring(book.getImage().indexOf("\\book-covers")))
                                        .build();
                        List<BookDTO.Review> reviewList = book.getReviews().stream()
                                        .filter(reviewEntity -> reviewEntity.isApproved())
                                        .map(reviewEntity -> BookDTO.Review.builder()
                                                        .postedAt(reviewEntity.getPostedAt())
                                                        .name(reviewEntity.getName())
                                                        .comment(reviewEntity.getComment())
                                                        .rate(reviewEntity.getRate())
                                                        .build())
                                        .toList();
                        bookDto.setReviewList(reviewList);
                        Optional<BookDTO> optBookDto = Optional.of(bookDto);
                        return optBookDto;
                } else {
                        return Optional.empty();
                }
        }
}
