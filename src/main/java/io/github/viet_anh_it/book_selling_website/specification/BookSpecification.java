package io.github.viet_anh_it.book_selling_website.specification;

import org.springframework.data.jpa.domain.Specification;

import io.github.viet_anh_it.book_selling_website.model.Book;
import io.github.viet_anh_it.book_selling_website.model.Book_;
import io.github.viet_anh_it.book_selling_website.model.Category;
import io.github.viet_anh_it.book_selling_website.model.Category_;
import io.github.viet_anh_it.book_selling_website.model.ReviewStat;
import io.github.viet_anh_it.book_selling_website.model.ReviewStat_;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

    public static Specification<Book> hasCategoryId(Long categoryId) {
        return (r, cq, cb) -> {
            Join<Book, Category> category = r.join(Book_.CATEGORY, JoinType.INNER);
            Expression<Long> categoryIdColumn = category.get(Category_.ID);
            Expression<Long> queryCondition = cb.literal(categoryId);
            Predicate p = cb.equal(categoryIdColumn, queryCondition);
            return p;
        };
    }

    public static Specification<Book> hasRate(Integer rate) {
        return (r, cq, cb) -> {
            Join<Book, ReviewStat> reviewStat = r.join(Book_.REVIEW_STAT, JoinType.INNER);
            Expression<Integer> averageColumn = reviewStat.get(ReviewStat_.AVERAGE_RATING_POINT);
            Expression<Integer> queryCondition = cb.literal(rate);
            Predicate p = cb.equal(averageColumn, queryCondition);
            return p;
        };
    }

    public static Specification<Book> hasKeyword(String keyword) {
        return (r, cq, cb) -> {
            Expression<String> isbnColumn = cb.lower(r.get(Book_.ISBN));
            Expression<String> nameColumn = cb.lower(r.get(Book_.NAME));
            Expression<String> authorColumn = cb.lower(r.get(Book_.AUTHOR_NAME));
            Expression<String> publisherColumn = cb.lower(r.get(Book_.PUBLISHER_NAME));
            Expression<String> pattern = cb.literal("%" + keyword.toLowerCase() + "%");
            Predicate isbnLike = cb.like(isbnColumn, pattern);
            Predicate nameLike = cb.like(nameColumn, pattern);
            Predicate authorLike = cb.like(authorColumn, pattern);
            Predicate publisherLike = cb.like(publisherColumn, pattern);
            Predicate predicate = cb.or(isbnLike, nameLike, authorLike, publisherLike);
            return predicate;
        };
    }

    public static Specification<Book> isInStock(Boolean stockStatus) {
        return (r, cq, cb) -> {
            if (stockStatus.booleanValue()) {
                Expression<Integer> stockQuantityColumn = r.get(Book_.STOCK_QUANTITY);
                Expression<Integer> zero = cb.literal(Integer.valueOf(0));
                Predicate predicate = cb.greaterThan(stockQuantityColumn, zero);
                return predicate;
            } else {
                Expression<Integer> stockQuantityColumn = r.get(Book_.STOCK_QUANTITY);
                Expression<Integer> zero = cb.literal(Integer.valueOf(0));
                Predicate predicate = cb.lessThanOrEqualTo(stockQuantityColumn, zero);
                return predicate;
            }
        };
    }
}
