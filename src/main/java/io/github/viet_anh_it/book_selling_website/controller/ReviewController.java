package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.ReviewDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Review_;
import io.github.viet_anh_it.book_selling_website.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    static int PAGE = 0;
    static int SIZE = 6;
    static String PROPERTY = Review_.POSTED_AT;
    static Direction DIRECTION = Direction.DESC;
    static Sort SORT = Sort.by(DIRECTION, PROPERTY);
    static Pageable PAGEABLE = PageRequest.of(PAGE, SIZE, SORT);
    static Boolean APPROVED = Boolean.FALSE;

    ReviewService reviewService;

    @Secured({ "ROLE_MANAGER" })
    @GetMapping("/manager/reviews")
    public String getBookReviewsPage(Model model) {
        SuccessResponse<List<ReviewDTO>> successResponse = this.reviewService.fetchAllReviews(PAGEABLE,
                Optional.of(APPROVED));
        List<ReviewDTO> reviewDtoList = successResponse.getData();
        PaginationMetadataDTO paginationMetadataDTO = successResponse.getPaginationMetadata();
        model.addAttribute("reviewDtoList", reviewDtoList);
        model.addAttribute("paginationMetadata", paginationMetadataDTO);
        return "bookReviews";
    }
}
