package io.github.viet_anh_it.book_selling_website.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.github.viet_anh_it.book_selling_website.validator.annotation.BookIdExist;
import io.github.viet_anh_it.book_selling_website.validator.annotation.ValidEmailFormat;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.First;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Second;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Third;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@JsonInclude(value = Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDTO implements Serializable {

    Long reviewId;

    @NotNull(groups = { First.class })
    @PositiveOrZero(groups = { Second.class })
    @BookIdExist(groups = { Third.class })
    @JsonInclude(value = Include.NON_DEFAULT)
    Long bookId;

    @NotBlank(groups = { First.class }, message = "Tên hiển thị không được trống!")
    String reviewerDisplayName;

    @NotBlank(groups = { First.class }, message = "Email không được trống!")
    @ValidEmailFormat(groups = { Second.class }, message = "Email không đúng định dạng!")
    String reviewerEmail;

    @NotBlank(groups = { First.class }, message = "Bình luận không được trống!")
    String reviewerComment;

    @NotNull(groups = { First.class }, message = "Vui lòng cho điểm đánh giá!")
    @Min(value = 1, groups = { Second.class }, message = "Tải lại trang và thử lại!")
    @Max(value = 5, groups = { Second.class }, message = "Tải lại trang và thử lại!")
    Integer givenRatingPoint;

    LocalDateTime postedAt;
    boolean approved;
    User user;
    Book book;

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class User {
        @NonNull
        String email;
    }

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Book {
        @NonNull
        String name;
    }
}
