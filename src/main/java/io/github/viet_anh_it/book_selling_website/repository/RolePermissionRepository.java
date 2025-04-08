package io.github.viet_anh_it.book_selling_website.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.viet_anh_it.book_selling_website.model.Permission;
import io.github.viet_anh_it.book_selling_website.model.Role;
import io.github.viet_anh_it.book_selling_website.model.RolePermission;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    @Query("select rp from RolePermission rp where rp.role.id = :roleId and rp.permission.id = :permissionId")
    Optional<RolePermission> findByRoleIdAndPermissionId(@Param("roleId") long roleId,
            @Param("permissionId") long permissionId);

    boolean existsByRoleAndPermission(Role role, Permission permission);
}
