package io.github.viet_anh_it.book_selling_website.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.viet_anh_it.book_selling_website.enums.PermissionEnum;
import io.github.viet_anh_it.book_selling_website.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("select p from Permission p inner join RolePermission rp on p.id = rp.permission.id where rp.role.id = :roleId")
    Optional<Set<Permission>> findByRoleId(@Param("roleId") long roleId);

    boolean existsByName(PermissionEnum permissionName);

    Optional<Permission> findByName(PermissionEnum permissionName);
}
