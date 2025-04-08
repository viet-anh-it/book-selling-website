package io.github.viet_anh_it.book_selling_website.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.viet_anh_it.book_selling_website.enums.RoleEnum;
import io.github.viet_anh_it.book_selling_website.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleEnum roleName);

    boolean existsByName(RoleEnum roleName);
}
