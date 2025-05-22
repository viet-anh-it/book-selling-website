package io.github.viet_anh_it.book_selling_website.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.viet_anh_it.book_selling_website.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    @Query("select r from Review r where r.user.id=:userId and r.book.id=:bookId")
    Optional<Review> findByUserIdAndBookId(@Param("userId") long userId, @Param("bookId") long bookId);
}
