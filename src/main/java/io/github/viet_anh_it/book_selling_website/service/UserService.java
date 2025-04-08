package io.github.viet_anh_it.book_selling_website.service;

import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.model.User;

public interface UserService {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    User save(User user);
}
