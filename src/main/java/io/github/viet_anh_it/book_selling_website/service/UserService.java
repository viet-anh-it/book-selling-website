package io.github.viet_anh_it.book_selling_website.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import io.github.viet_anh_it.book_selling_website.dto.UserDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.User;

public interface UserService {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    User save(User user);

    SuccessResponse<List<UserDTO>> getAllUsers(Pageable pageable);
}
