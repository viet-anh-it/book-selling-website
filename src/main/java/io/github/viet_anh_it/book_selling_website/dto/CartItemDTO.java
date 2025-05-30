package io.github.viet_anh_it.book_selling_website.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.github.viet_anh_it.book_selling_website.validator.annotation.BookIdExist;
import io.github.viet_anh_it.book_selling_website.validator.annotation.LteBookStockQuantity;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.OnCreate;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.First;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Fourth;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Second;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Third;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@LteBookStockQuantity(groups = { Fourth.class })
public class CartItemDTO implements Serializable {

    @NotNull(groups = { First.class, OnCreate.class })
    @PositiveOrZero(groups = { Second.class, OnCreate.class })
    @BookIdExist(groups = { Third.class, OnCreate.class })
    Long bookId;

    Long cartItemId;
    String image;
    String name;

    @NotNull(groups = { First.class }, message = "Không được để trống số lượng!")
    @Min(value = 1, groups = { Second.class }, message = "Số lượng tối thiểu phải là {value}!")
    Integer addToCartQuantity;

    Integer price;
    Integer totalPrice;
}
