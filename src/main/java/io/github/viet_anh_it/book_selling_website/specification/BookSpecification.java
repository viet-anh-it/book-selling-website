package io.github.viet_anh_it.book_selling_website.specification;

import org.springframework.data.jpa.domain.Specification;

import io.github.viet_anh_it.book_selling_website.model.Book;
import io.github.viet_anh_it.book_selling_website.model.Book_;

public class BookSpecification {

    public static Specification<Book> hasPriceInRange(Integer min, Integer max) {
        return (r, cq, cb) -> {
            return cb.and(cb.greaterThanOrEqualTo(r.get(Book_.PRICE), min),
                    cb.lessThanOrEqualTo(r.get(Book_.PRICE), max));
        };
    }
}
