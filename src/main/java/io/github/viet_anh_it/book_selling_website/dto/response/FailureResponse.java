package io.github.viet_anh_it.book_selling_website.dto.response;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FailureResponse<T> implements Serializable {

    int status;
    String error;
    T detail;
}
