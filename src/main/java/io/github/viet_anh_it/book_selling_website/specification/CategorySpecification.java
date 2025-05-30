package io.github.viet_anh_it.book_selling_website.specification;

import org.springframework.data.jpa.domain.Specification;

import io.github.viet_anh_it.book_selling_website.model.Category;
import io.github.viet_anh_it.book_selling_website.model.Category_;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public class CategorySpecification {

    public static Specification<Category> hasNameLike(String name) {
        return (categoryRoot, criteriaQuery, criteriaBuilder) -> {
            Expression<String> nameColumn = criteriaBuilder.lower(categoryRoot.get(Category_.NAME));
            Expression<String> wildcardPattern = criteriaBuilder.literal("%" + name.toLowerCase() + "%");
            Predicate predicate = criteriaBuilder.like(nameColumn, wildcardPattern);
            return predicate;
        };
    }
}
