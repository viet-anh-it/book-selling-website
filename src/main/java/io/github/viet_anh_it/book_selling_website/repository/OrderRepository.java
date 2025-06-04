package io.github.viet_anh_it.book_selling_website.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import io.github.viet_anh_it.book_selling_website.model.Order;
import io.github.viet_anh_it.book_selling_website.model.Order_;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.model.User_;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // @Query("select o from Order o where o.user.email = :userEmail")
    // Page<Order> getPersonalOrders(@Param("userEmail") String userEmail, Pageable pageable, Specification<Order> spec);

    default Page<Order> getPersonalOrders(String username, Pageable pageable, Specification<Order> combinedSpec) {
        Specification<Order> hasUser = (root, query, cb) -> {
            Join<Order, User> user = root.join(Order_.USER, JoinType.INNER);
            Expression<String> emailColumn = user.get(User_.EMAIL);
            Expression<String> value = cb.literal(username);
            Predicate predicate = cb.equal(emailColumn, value);
            return predicate;
        };
        combinedSpec = combinedSpec.and(hasUser);
        return findAll(combinedSpec, pageable);
    }
}
