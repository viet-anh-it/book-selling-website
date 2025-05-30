package io.github.viet_anh_it.book_selling_website.specification;

import org.springframework.data.jpa.domain.Specification;

import io.github.viet_anh_it.book_selling_website.model.Book;
import io.github.viet_anh_it.book_selling_website.model.Book_;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public class BookSpecification {

    public static Specification<Book> hasPriceInRange(Integer min, Integer max) {
        return (r, cq, cb) -> {
            return cb.and(cb.greaterThanOrEqualTo(r.get(Book_.PRICE), min),
                    cb.lessThanOrEqualTo(r.get(Book_.PRICE), max));
        };
    }

    public static Specification<Book> isDeleted(Boolean isDeleted) {
        return (r, cq, cb) -> {
            Expression<Boolean> dbDeletedColumn = r.get(Book_.DELETED);
            Expression<Boolean> boolLiteral = cb.literal(isDeleted);
            Predicate p = cb.equal(dbDeletedColumn, boolLiteral);
            return p;
        };
    }
}
