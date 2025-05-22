package io.github.viet_anh_it.book_selling_website.dto;

import java.io.Serializable;

import io.github.viet_anh_it.book_selling_website.enums.SortDirectionEnum;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@SuppressWarnings("unused")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SortMetadataDTO implements Serializable {
    String fieldName;
    SortDirectionEnum sortDirection;
}
