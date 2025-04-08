package io.github.viet_anh_it.book_selling_website.service.impl;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.enums.PermissionEnum;
import io.github.viet_anh_it.book_selling_website.model.Permission;
import io.github.viet_anh_it.book_selling_website.repository.PermissionRepository;
import io.github.viet_anh_it.book_selling_website.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;

    @Override
    public Optional<Set<Permission>> findByRoleId(long roleId) {
        return this.permissionRepository.findByRoleId(roleId);
    }

    @Override
    public boolean existsByName(PermissionEnum permissionName) {
        return this.permissionRepository.existsByName(permissionName);
    }

    @Override
    public Permission save(Permission permissionEntity) {
        return this.permissionRepository.save(permissionEntity);
    }

    @Override
    public Optional<Permission> findByName(PermissionEnum permissionName) {
        return this.permissionRepository.findByName(permissionName);
    }

}
