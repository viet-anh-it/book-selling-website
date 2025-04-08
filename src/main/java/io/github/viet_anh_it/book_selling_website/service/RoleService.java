package io.github.viet_anh_it.book_selling_website.service;

import java.util.Optional;
import java.util.Set;

import io.github.viet_anh_it.book_selling_website.dto.response.ReadAllRolesResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.enums.RoleEnum;
import io.github.viet_anh_it.book_selling_website.model.Role;

public interface RoleService {

    Role save(Role roleEntity);

    Optional<Role> findByName(RoleEnum roleName);

    boolean existsByName(RoleEnum roleName);

    SuccessResponse<Set<ReadAllRolesResponseDTO>> readAllRoles();
}
