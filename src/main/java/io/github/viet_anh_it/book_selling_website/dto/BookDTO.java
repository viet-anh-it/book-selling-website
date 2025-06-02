package io.github.viet_anh_it.book_selling_website.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.github.viet_anh_it.book_selling_website.validator.annotation.group.OnCreate;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.OnUpdate;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.First;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Second;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@JsonInclude(value = Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookDTO implements Serializable {

    @JsonInclude(value = Include.NON_DEFAULT)
    long id;

    String image;

    @NotBlank(message = "ISBN không được trống!", groups = { OnCreate.class, OnUpdate.class })
    String isbn;

    @NotBlank(message = "Tên sách không được trống!", groups = { OnCreate.class, OnUpdate.class })
    String name;

    @NotBlank(message = "Tên tác giả không được trống!", groups = { OnCreate.class, OnUpdate.class })
    String author;

    @NotBlank(message = "Tên nhà xuất bản không được trống!", groups = { OnCreate.class, OnUpdate.class })
    String publisher;

    @NotNull(message = "Số lượng tồn kho không được trống!", groups = { OnCreate.class, OnUpdate.class, First.class })
    @Min(value = 1, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 1!", groups = { OnCreate.class, OnUpdate.class, Second.class })
    Integer stockQuantity;

    Integer soldQuantity;

    @NotNull(message = "Giá bán không được trống!", groups = { OnCreate.class })
    @Min(value = 1000, message = "Giá bán phải lớn hơn hoặc bằng 1000!", groups = { OnCreate.class, OnUpdate.class, Second.class })
    Integer price;

    @NotBlank(message = "Mô tả không được trống!", groups = { OnCreate.class, OnUpdate.class })
    String description;

    boolean deleted;
    BookDTO.Category category;
    int averageRatingPoint;
    int totalReviews;
    List<BookDTO.Review> reviewList;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Category {
        Long id;
        String name;
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Review {
        LocalDateTime postedAt;
        String name;
        String comment;
        Integer rate;
    }
}
