package io.github.viet_anh_it.book_selling_website.dto.request;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogInRequestDTO implements Serializable {

    @NotBlank(message = "Email không được trống!")
    @Email(message = "Email không đúng định dạng!")
    String email;

    @NotBlank(message = "Mật khẩu không được trống!")
    @Length(min = 8, message = "Mật khẩu phải có tối thiểu 8 ký tự!")
    String password;
}
