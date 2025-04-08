package io.github.viet_anh_it.book_selling_website.exception;

public class EmailAlreadyExistedException extends RuntimeException {

    public EmailAlreadyExistedException(String message) {
        super(message);
    }
}
