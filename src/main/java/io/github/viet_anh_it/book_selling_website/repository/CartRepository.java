package io.github.viet_anh_it.book_selling_website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.viet_anh_it.book_selling_website.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

}
