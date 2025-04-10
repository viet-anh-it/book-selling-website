package io.github.viet_anh_it.book_selling_website.dto.response;

import java.io.Serializable;

import io.github.viet_anh_it.book_selling_website.enums.ErrorTypeEnum;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FailureResponse<T> implements Serializable {

    int status;
    String error;
    ErrorTypeEnum type;
    T detail;
}
