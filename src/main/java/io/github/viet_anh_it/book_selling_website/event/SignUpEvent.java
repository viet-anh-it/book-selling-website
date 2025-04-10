package io.github.viet_anh_it.book_selling_website.event;

import org.springframework.context.ApplicationEvent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignUpEvent extends ApplicationEvent {

    String userEmail;
    String appContextPath;

    public SignUpEvent(String userEmail, String appContextPath) {
        super(new Object());
        this.userEmail = userEmail;
        this.appContextPath = appContextPath;
    }

}
