package io.github.viet_anh_it.book_selling_website.dto.request;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import io.github.viet_anh_it.book_selling_website.validator.annotation.ValidEmailFormat;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.First;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Second;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignUpRequestDTO implements Serializable {

    @ValidEmailFormat
    String email;

    @NotBlank(message = "Mật khẩu không được trống!", groups = { First.class })
    @Length(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự!", groups = { Second.class })
    String password;
}
