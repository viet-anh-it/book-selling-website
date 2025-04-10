package io.github.viet_anh_it.book_selling_website.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TokenTypeEnum {
    ACCESS("access"),
    REFRESH("refresh"),
    REGISTRATION_CONFIRMATION("registration_confirmation"),
    RESET_PASSWORD("reset_password");

    String name;
}
