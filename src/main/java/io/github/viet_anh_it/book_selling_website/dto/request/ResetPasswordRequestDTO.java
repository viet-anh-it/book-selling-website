package io.github.viet_anh_it.book_selling_website.dto.request;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import io.github.viet_anh_it.book_selling_website.enums.TokenTypeEnum;
import io.github.viet_anh_it.book_selling_website.validator.annotation.PasswordMatch;
import io.github.viet_anh_it.book_selling_website.validator.annotation.ValidToken;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.First;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Fourth;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Second;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Third;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@PasswordMatch(firstPasswordFieldName = "password", secondPasswordFieldName = "confirmPassword", groups = {
                Fourth.class })
public class ResetPasswordRequestDTO implements Serializable {

        @ValidToken(tokenType = TokenTypeEnum.FORGOT_PASSWORD, groups = {
                        First.class }, message = "Có lỗi xảy ra! Nhấn Quên mật khẩu và thử lại!")
        String token;

        @NotBlank(message = "Mật khẩu không được trống!", groups = { Second.class })
        @Length(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự!", groups = { Third.class })
        String password;

        @NotBlank(message = "Mật khẩu không được trống!", groups = { Second.class })
        @Length(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự!", groups = { Third.class })
        String confirmPassword;
}
