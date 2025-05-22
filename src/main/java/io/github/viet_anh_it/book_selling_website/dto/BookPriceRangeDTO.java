package io.github.viet_anh_it.book_selling_website.dto;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookPriceRangeDTO implements Serializable {
    Integer minPrice;
    Integer maxPrice;
}
