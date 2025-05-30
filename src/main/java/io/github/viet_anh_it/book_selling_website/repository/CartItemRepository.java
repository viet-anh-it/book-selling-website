package io.github.viet_anh_it.book_selling_website.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.viet_anh_it.book_selling_website.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select ci from CartItem ci inner join Cart c on ci.cart.id = c.id where c.user.id = :userId and ci.book.id = :bookId")
    Optional<CartItem> findIfBookAlreadyInCart(@Param("userId") long userId, @Param("bookId") long bookId);
}
