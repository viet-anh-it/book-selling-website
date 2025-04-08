package io.github.viet_anh_it.book_selling_website.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.model.Permission;
import io.github.viet_anh_it.book_selling_website.model.Role;
import io.github.viet_anh_it.book_selling_website.model.RolePermission;
import io.github.viet_anh_it.book_selling_website.repository.RolePermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RolePermissionServiceImpl implements RolePermissionService {

    RolePermissionRepository rolePermissionRepository;

    @Override
    public boolean existsByRoleAndPermission(Role role, Permission permission) {
        return this.rolePermissionRepository.existsByRoleAndPermission(role, permission);
    }

    @Override
    public RolePermission save(RolePermission rolePermissionEntity) {
        return this.rolePermissionRepository.save(rolePermissionEntity);
    }

    @Override
    public Optional<RolePermission> findByRoleIdAndPermissionId(long roleId, long permissionId) {
        return this.rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId);
    }

}
