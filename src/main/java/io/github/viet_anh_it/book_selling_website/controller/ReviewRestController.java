package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.viet_anh_it.book_selling_website.dto.ReviewDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Review_;
import io.github.viet_anh_it.book_selling_website.service.ReviewService;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.ValidationOrder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewRestController {

        ReviewService reviewService;

        @Secured({ "ROLE_CUSTOMER" })
        @PostMapping("/reviews")
        public ResponseEntity<SuccessResponse<Void>> postReview(Authentication authentication,
                        @RequestBody @Validated({ ValidationOrder.class }) ReviewDTO reviewDto) {
                this.reviewService.postReview(authentication, reviewDto);
                SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Gửi đánh giá thành công!")
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED.value()).body(successResponse);
        }

        @Secured({ "ROLE_MANAGER" })
        @PatchMapping("/reviews/approve/{reviewId}")
        public ResponseEntity<SuccessResponse<List<ReviewDTO>>> approveReview(
                        @PathVariable(name = "reviewId") Long reviewId) {
                this.reviewService.approveReview(reviewId);
                SuccessResponse<List<ReviewDTO>> successResponse = SuccessResponse.<List<ReviewDTO>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Duyệt đánh giá thành công!")
                                .build();
                return ResponseEntity.status(HttpStatus.OK.value()).body(successResponse);
        }

        @Secured({ "ROLE_MANAGER" })
        @GetMapping("/reviews")
        public ResponseEntity<SuccessResponse<List<ReviewDTO>>> fetchAllReviews(
                        @RequestParam(name = "approved") Optional<Boolean> optApprovedParam,
                        @PageableDefault(page = 0, size = 6, sort = Review_.POSTED_AT, direction = Direction.DESC) Pageable pageable) {
                SuccessResponse<List<ReviewDTO>> successResponse = this.reviewService
                                .fetchAllReviews(pageable, optApprovedParam);
                return ResponseEntity.status(HttpStatus.OK.value()).body(successResponse);
        }

        @Secured({ "ROLE_CUSTOMER", "ROLE_MANAGER" })
        @DeleteMapping("/reviews/{reviewId}")
        public ResponseEntity<SuccessResponse<Void>> deleteReview(@PathVariable(name = "reviewId") Long reviewId) {
                this.reviewService.deleteReview(reviewId);
                SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Xóa đánh giá thành công!")
                                .build();
                return ResponseEntity.status(HttpStatus.OK.value()).body(successResponse);
        }
}
