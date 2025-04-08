package io.github.viet_anh_it.book_selling_website.service;

import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.model.Permission;
import io.github.viet_anh_it.book_selling_website.model.Role;
import io.github.viet_anh_it.book_selling_website.model.RolePermission;

public interface RolePermissionService {
    Optional<RolePermission> findByRoleIdAndPermissionId(long roleId, long permissionId);

    boolean existsByRoleAndPermission(Role role, Permission permission);

    RolePermission save(RolePermission rolePermissionEntity);
}
