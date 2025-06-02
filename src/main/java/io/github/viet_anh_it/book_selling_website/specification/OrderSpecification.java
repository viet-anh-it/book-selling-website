package io.github.viet_anh_it.book_selling_website.specification;

import org.springframework.data.jpa.domain.Specification;

import io.github.viet_anh_it.book_selling_website.model.Order;
import io.github.viet_anh_it.book_selling_website.model.Order_;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public class OrderSpecification {

    public static Specification<Order> hasStatus(String status) {
        return (r, cq, cb) -> {
            Expression<String> statusColumn = r.get(Order_.STATUS);
            Expression<String> statusValue = cb.literal(status);
            Predicate predicate = cb.equal(statusColumn, statusValue);
            return predicate;
        };
    }

    public static Specification<Order> hasKeyword(String keyword) {
        return (r, cq, cb) -> {
            Expression<String> codeColumn = cb.lower(r.get(Order_.CODE));
            Expression<String> phoneColumn = cb.lower(r.get(Order_.PHONE));
            Expression<String> pattern = cb.literal("%" + keyword.toLowerCase() + "%");
            Predicate codeLike = cb.like(codeColumn, pattern);
            Predicate phoneLike = cb.like(phoneColumn, pattern);
            Predicate predicate = cb.or(codeLike, phoneLike);
            return predicate;
        };
    }
}
