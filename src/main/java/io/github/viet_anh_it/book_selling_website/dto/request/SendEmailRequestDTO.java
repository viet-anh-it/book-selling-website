package io.github.viet_anh_it.book_selling_website.dto.request;

import java.io.Serializable;

import io.github.viet_anh_it.book_selling_website.validator.annotation.EmailExist;
import io.github.viet_anh_it.book_selling_website.validator.annotation.ValidEmailFormat;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.First;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Second;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmailRequestDTO implements Serializable {

    @ValidEmailFormat(groups = { First.class })
    @EmailExist(groups = { Second.class })
    String email;
}
