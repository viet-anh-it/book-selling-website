package io.github.viet_anh_it.book_selling_website.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.ReviewDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Book;
import io.github.viet_anh_it.book_selling_website.model.Review;
import io.github.viet_anh_it.book_selling_website.model.ReviewStat;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.repository.BookRepository;
import io.github.viet_anh_it.book_selling_website.repository.ReviewRepository;
import io.github.viet_anh_it.book_selling_website.service.ReviewService;
import io.github.viet_anh_it.book_selling_website.service.ReviewStatService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import io.github.viet_anh_it.book_selling_website.specification.ReviewSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {

    static Boolean APPROVED = Boolean.FALSE;
    static Specification<Review> REVIEW_SPEC = ReviewSpecification.isApproved(APPROVED);

    UserService userService;
    BookRepository bookRepository;
    ReviewRepository reviewRepository;
    ReviewStatService reviewStatService;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('POST_REVIEW')")
    public void postReview(Authentication authentication, ReviewDTO reviewDTO) {
        User currentLoggedInUser = this.userService.findByEmail(authentication.getName()).get();
        Book book = this.bookRepository.findById(reviewDTO.getBookId()).get();
        Optional<Review> optReview = this.reviewRepository
                .findByUserIdAndBookId(currentLoggedInUser.getId(), book.getId());
        if (optReview.isPresent()) {
            throw new RuntimeException("Bạn đã đánh giá cho sản phẩm này rồi!");
        } else {
            Review review = new Review();
            review.setName(reviewDTO.getReviewerDisplayName());
            review.setComment(reviewDTO.getReviewerComment());
            review.setRate(reviewDTO.getGivenRatingPoint());
            review.setPostedAt(LocalDateTime.now());
            review.setApproved(false);

            review.setUser(currentLoggedInUser);
            review.setBook(book);
            this.reviewRepository.save(review);
        }
    }

    private ReviewStat updateReviewStat(ReviewStat reviewStat, Integer givenRatingPoint) {
        reviewStat.setTotalReviews(reviewStat.getTotalReviews() + 1);
        reviewStat.setTotalRatingPoint(reviewStat.getTotalRatingPoint() + givenRatingPoint);
        reviewStat.setAverageRatingPoint(reviewStat.getTotalRatingPoint() / reviewStat.getTotalReviews());
        Map<Integer, Integer> countByRatingPoint = reviewStat.getCountByRatingPoint();
        countByRatingPoint.put(givenRatingPoint, countByRatingPoint.get(givenRatingPoint) + 1);
        return reviewStat;
    }

    @Override
    @PreAuthorize("hasAuthority('GET_REVIEW')")
    public SuccessResponse<List<ReviewDTO>> fetchAllReviews(Pageable pageable, Optional<Boolean> optApprovedParam) {
        Specification<Review> reviewSpec = REVIEW_SPEC;
        if (optApprovedParam.isPresent()) {
            reviewSpec = ReviewSpecification.isApproved(optApprovedParam.get());
        }
        Page<Review> reviewEntityPage = this.reviewRepository.findAll(reviewSpec, pageable);
        List<Review> reviewEntityList = reviewEntityPage.getContent();
        List<ReviewDTO> reviewDtoList = reviewEntityList.stream()
                .map(reviewEntity -> ReviewDTO.builder()
                        .reviewId(reviewEntity.getId())
                        .givenRatingPoint(reviewEntity.getRate())
                        .reviewerComment(reviewEntity.getComment())
                        .postedAt(reviewEntity.getPostedAt())
                        .approved(reviewEntity.isApproved())
                        .user(new ReviewDTO.User(reviewEntity.getUser().getEmail()))
                        .book(new ReviewDTO.Book(reviewEntity.getBook().getName()))
                        .build())
                .toList();
        PaginationMetadataDTO paginationMetadataDTO = PaginationMetadataDTO.builder()
                .currentPosition(reviewEntityPage.getNumber())
                .numberOfElementsPerPage(reviewEntityPage.getSize())
                .totalNumberOfElements(reviewEntityPage.getTotalElements())
                .totalNumberOfPages(reviewEntityPage.getTotalPages())
                .hasNextPage(reviewEntityPage.hasNext())
                .hasPreviousPage(reviewEntityPage.hasPrevious())
                .nextPosition(reviewEntityPage.hasNext() ? reviewEntityPage.getNumber() + 1 : -1)
                .previousPosition(reviewEntityPage.hasPrevious() ? reviewEntityPage.getNumber() - 1 : -1)
                .build();
        SuccessResponse<List<ReviewDTO>> successResponse = SuccessResponse.<List<ReviewDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách đánh giá thành công!")
                .data(reviewDtoList)
                .paginationMetadata(paginationMetadataDTO)
                .build();
        return successResponse;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('APPROVE_REVIEW')")
    public void approveReview(Long reviewId) {
        Optional<Review> optReviewEntity = this.reviewRepository.findById(reviewId);
        if (optReviewEntity.isPresent()) {
            Review reviewEntity = optReviewEntity.get();
            reviewEntity.setApproved(true);
            this.reviewRepository.save(reviewEntity);

            Book book = reviewEntity.getBook();
            ReviewStat reviewStat = book.getReviewStat();
            if (reviewStat == null) {
                reviewStat = new ReviewStat();
                reviewStat = this.updateReviewStat(reviewStat, reviewEntity.getRate());
                book.setReviewStat(reviewStat);
                this.reviewStatService.save(reviewStat);
                this.bookRepository.save(book);
            } else {
                reviewStat = this.updateReviewStat(reviewStat, reviewEntity.getRate());
                this.reviewStatService.save(reviewStat);
            }
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('DELETE_REVIEW')")
    public void deleteReview(Long reviewId) {
        Optional<Review> optReviewEntity = this.reviewRepository.findById(reviewId);
        if (optReviewEntity.isPresent()) {
            Review reviewEntity = optReviewEntity.get();
            Book bookEntity = reviewEntity.getBook();
            ReviewStat reviewStatEntity = bookEntity.getReviewStat();
            reviewStatEntity.setTotalReviews(reviewStatEntity.getTotalReviews() - 1);
            reviewStatEntity.setTotalRatingPoint(reviewStatEntity.getTotalRatingPoint() - reviewEntity.getRate());
            reviewStatEntity
                    .setAverageRatingPoint(reviewStatEntity.getTotalReviews() == 0 ? 0
                            : reviewStatEntity
                                    .getTotalRatingPoint() / reviewStatEntity.getTotalReviews());
            Map<Integer, Integer> countByRatingPoint = reviewStatEntity.getCountByRatingPoint();
            countByRatingPoint.put(reviewEntity.getRate(), countByRatingPoint.get(reviewEntity.getRate()) - 1);
            this.reviewRepository.deleteById(reviewId);
            // nếu không gọi save thì reviewStatEntity vẫn được tự động update vì nó đang ở
            // trạng thái managed và nằm trong Persistence Context
            // Hibernate tự động dirty checking và sinh câu lệnh SQL UPDATE khi flush xuống
            // database
            this.reviewStatService.save(reviewStatEntity);
        }
    }
}
