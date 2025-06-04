package io.github.viet_anh_it.book_selling_website.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.github.viet_anh_it.book_selling_website.dto.BookDTO;
import io.github.viet_anh_it.book_selling_website.dto.BookPriceRangeDTO;
import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Book;
import io.github.viet_anh_it.book_selling_website.model.Category;
import io.github.viet_anh_it.book_selling_website.repository.BookRepository;
import io.github.viet_anh_it.book_selling_website.repository.CategoryRepository;
import io.github.viet_anh_it.book_selling_website.service.BookService;
import io.github.viet_anh_it.book_selling_website.specification.BookSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookServiceImpl implements BookService {

        @NonFinal
        @Value("${app.upload-dir.image.book-cover}")
        String bookCoverImageUploadDir;

        BookRepository bookRepository;
        CategoryRepository categoryRepository;

        @Override
        @Transactional
        @PreAuthorize("hasAuthority('CREATE_BOOK')")
        public void createBook(BookDTO bookDTO, MultipartFile bookCoverImage) throws IOException {
                @SuppressWarnings("null")
                String cleanFileName = StringUtils.cleanPath(bookCoverImage.getOriginalFilename());
                String fileExtension = StringUtils.getFilenameExtension(cleanFileName);
                String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

                Path uploadPath = Paths.get(this.bookCoverImageUploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);
                Path targetLocation = uploadPath.resolve(uniqueFileName);
                bookCoverImage.transferTo(targetLocation.toFile());

                Book book = Book.builder()
                                .isbn(bookDTO.getIsbn())
                                .name(bookDTO.getName())
                                .authorName(bookDTO.getAuthor())
                                .publisherName(bookDTO.getPublisher())
                                .price(bookDTO.getPrice())
                                .stockQuantity(bookDTO.getStockQuantity())
                                .image(targetLocation.toAbsolutePath().toString())
                                .description(bookDTO.getDescription())
                                .deleted(false)
                                .build();
                Category category = this.categoryRepository.findById(bookDTO.getCategory().getId()).get();
                book.setCategory(category);
                this.bookRepository.save(book);
        }

        @Override
        public SuccessResponse<List<BookDTO>> getAllBooks(Pageable pageable, Optional<BookPriceRangeDTO> optPriceParam,
                        Optional<Long> optCategoryParam, Optional<Integer> optRateParam, Optional<String> optKeywordParam,
                        Optional<Boolean> optStockParam) {
                // build specification
                Specification<Book> isDeleted = BookSpecification.isDeleted(Boolean.FALSE);
                Specification<Book> combinedBookSpec = Specification.where(isDeleted);
                if (optPriceParam.isPresent()) {
                        BookPriceRangeDTO bookPriceRangeDTO = optPriceParam.get();
                        Specification<Book> hasPriceInRange = BookSpecification.hasPriceInRange(bookPriceRangeDTO.getMinPrice(), bookPriceRangeDTO.getMaxPrice());
                        combinedBookSpec = combinedBookSpec.and(hasPriceInRange);
                }

                if (optCategoryParam.isPresent()) {
                        Long categoryId = optCategoryParam.get();
                        Specification<Book> hasCategoryId = BookSpecification.hasCategoryId(categoryId);
                        combinedBookSpec = combinedBookSpec.and(hasCategoryId);
                }

                if (optRateParam.isPresent()) {
                        Integer rate = optRateParam.get();
                        if (rate.intValue() > 0) {
                                Specification<Book> hasRate = BookSpecification.hasRate(rate);
                                combinedBookSpec = combinedBookSpec.and(hasRate);
                        }
                }

                if (optKeywordParam.isPresent()) {
                        String keyword = optKeywordParam.get();
                        if (!keyword.trim().equals("")) {
                                Specification<Book> hasKeyword = BookSpecification.hasKeyword(keyword);
                                combinedBookSpec = combinedBookSpec.and(hasKeyword);
                        }
                }

                if (optStockParam.isPresent()) {
                        Boolean stockStatus = optStockParam.get();
                        Specification<Book> isInStock = BookSpecification.isInStock(stockStatus);
                        combinedBookSpec = combinedBookSpec.and(isInStock);
                }
                // end build specification

                Page<Book> pagedBooks = this.bookRepository.findAll(combinedBookSpec, pageable);
                List<BookDTO> bookDtoList = pagedBooks.getContent()
                                .stream()
                                // .filter(book -> !book.isDeleted())
                                .map(book -> BookDTO.builder()
                                                .id(book.getId())
                                                .isbn(book.getIsbn())
                                                .name(book.getName())
                                                .author(book.getAuthorName())
                                                .publisher(book.getPublisherName())
                                                .price(book.getPrice())
                                                .image(book.getImage().substring(book.getImage().indexOf("\\book-covers")))
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
                                        .isbn(book.getIsbn())
                                        .name(book.getName())
                                        .price(book.getPrice())
                                        .author(book.getAuthorName())
                                        .publisher(book.getPublisherName())
                                        .description(book.getDescription())
                                        .stockQuantity(book.getStockQuantity())
                                        .soldQuantity(book.getSoldQuantity())
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
                        BookDTO.Category category = BookDTO.Category.builder()
                                        .id(book.getCategory() != null ? book.getCategory().getId() : 0)
                                        .name(book.getCategory() != null ? book.getCategory().getName() : "")
                                        .build();
                        bookDto.setReviewList(reviewList);
                        bookDto.setCategory(category);
                        Optional<BookDTO> optBookDto = Optional.of(bookDto);
                        return optBookDto;
                } else {
                        return Optional.empty();
                }
        }

        @Override
        @Transactional
        @PreAuthorize("hasAuthority('UPDATE_BOOK')")
        public SuccessResponse<BookDTO> updateBookById(long bookId, BookDTO bookDto, MultipartFile bookCoverImage) throws IOException {
                Optional<Book> optBook = this.bookRepository.findById(bookId);
                SuccessResponse<BookDTO> successResponse = SuccessResponse.<BookDTO>builder()
                                .status(HttpStatus.OK.value())
                                .message("Cập nhật sách thành công!")
                                .build();
                if (optBook.isEmpty()) {
                        successResponse.setData(null);
                        return successResponse;
                }
                Book book = optBook.get();
                book.setName(bookDto.getName());
                book.setAuthorName(bookDto.getAuthor());
                book.setPublisherName(bookDto.getPublisher());
                book.setIsbn(bookDto.getIsbn());
                book.setStockQuantity(bookDto.getStockQuantity());
                book.setPrice(bookDto.getPrice());
                book.setDescription(bookDto.getDescription());

                @SuppressWarnings("null")
                String cleanFileName = StringUtils.cleanPath(bookCoverImage.getOriginalFilename());
                String fileExtension = StringUtils.getFilenameExtension(cleanFileName);
                Path uploadPath = Paths.get(this.bookCoverImageUploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);
                Path originalLocation = uploadPath.resolve(cleanFileName);
                String finalFileName;
                if (Files.exists(originalLocation)) {
                        finalFileName = cleanFileName;
                } else {
                        finalFileName = UUID.randomUUID().toString() + "." + fileExtension;
                }
                Path targetLocation = uploadPath.resolve(finalFileName);
                // bookCoverImage.transferTo(targetLocation.toFile());
                InputStream in = bookCoverImage.getInputStream();
                Files.copy(in, targetLocation, StandardCopyOption.REPLACE_EXISTING);

                book.setImage(targetLocation.toAbsolutePath().toString());

                Category category = this.categoryRepository.findById(bookDto.getCategory().getId()).get();
                book.setCategory(category);
                this.bookRepository.save(book);

                BookDTO responseBookDto = BookDTO.builder()
                                .image(book.getImage().substring(book.getImage().indexOf("\\book-covers")))
                                .isbn(book.getIsbn())
                                .name(book.getName())
                                .author(book.getAuthorName())
                                .publisher(book.getPublisherName())
                                .price(book.getPrice())
                                .build();
                successResponse.setData(responseBookDto);

                return successResponse;
        }

        @Override
        @Transactional
        @PreAuthorize("hasAuthority('DELETE_BOOK')")
        public void deleteBookById(long bookId) {
                Optional<Book> optBook = this.bookRepository.findById(bookId);
                if (optBook.isEmpty())
                        return;
                Book book = optBook.get();
                book.setDeleted(true);
                this.bookRepository.save(book);
        }

        @Override
        public int getStockQuantityByBookId(long bookId) {
                Optional<Book> optBook = this.bookRepository.findById(bookId);
                return optBook.isPresent() ? optBook.get().getStockQuantity() : 0;
        }
}
