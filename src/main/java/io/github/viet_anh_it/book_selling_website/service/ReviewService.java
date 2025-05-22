package io.github.viet_anh_it.book_selling_website.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import io.github.viet_anh_it.book_selling_website.dto.ReviewDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;

public interface ReviewService {
    void postReview(Authentication authentication, ReviewDTO reviewDTO);

    SuccessResponse<List<ReviewDTO>> fetchAllReviews(Pageable pageable, Optional<Boolean> optApprovedParam);

    void approveReview(Long reviewId);

    void deleteReview(Long reviewId);

    // Optional<Review> fetchSingleReviewById(Long reviewId);
}