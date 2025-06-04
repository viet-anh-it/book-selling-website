package io.github.viet_anh_it.book_selling_website.specification;

import org.springframework.data.jpa.domain.Specification;

import io.github.viet_anh_it.book_selling_website.model.Role;
import io.github.viet_anh_it.book_selling_website.model.Role_;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.model.User_;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class UserSpecification {

    public static Specification<User> notHaveRoleAdmin() {
        return (r, cq, cb) -> {
            Join<User, Role> role = r.join(User_.ROLE, JoinType.INNER);
            Expression<String> roleNameColumn = role.get(Role_.NAME);
            Expression<String> roleAdmin = cb.literal("ADMIN");
            Predicate predicate = cb.notEqual(roleNameColumn, roleAdmin);
            return predicate;
        };
    }
}
