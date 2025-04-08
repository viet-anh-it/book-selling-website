package io.github.viet_anh_it.book_selling_website.service;

import java.util.Optional;
import java.util.Set;

import io.github.viet_anh_it.book_selling_website.enums.PermissionEnum;
import io.github.viet_anh_it.book_selling_website.model.Permission;

public interface PermissionService {

    Optional<Set<Permission>> findByRoleId(long roleId);

    boolean existsByName(PermissionEnum permissionName);

    Permission save(Permission permissionEntity);

    Optional<Permission> findByName(PermissionEnum permissionName);

}
