package io.github.viet_anh_it.book_selling_website.specification;

import org.springframework.data.jpa.domain.Specification;

import io.github.viet_anh_it.book_selling_website.model.Review;
import io.github.viet_anh_it.book_selling_website.model.Review_;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public class ReviewSpecification {
    public static Specification<Review> isApproved(Boolean approved) {
        return (r, cq, cb) -> {
            Expression<Boolean> approvedDatabaseColumn = r.get(Review_.APPROVED);
            Expression<Boolean> booleanLiteral = cb.literal(approved);
            Predicate predicate = cb.equal(approvedDatabaseColumn, booleanLiteral);
            return predicate;
        };
    }
}
