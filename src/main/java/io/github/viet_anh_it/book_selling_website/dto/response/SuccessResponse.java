package io.github.viet_anh_it.book_selling_website.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.SortMetadataDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SuccessResponse<T> implements Serializable {

    int status;
    String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    PaginationMetadataDTO paginationMetadata;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    SortMetadataDTO sortMetadata;
}
