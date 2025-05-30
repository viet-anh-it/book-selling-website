package io.github.viet_anh_it.book_selling_website.model;

import java.util.Set;

import io.github.viet_anh_it.book_selling_website.enums.PermissionEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "permissions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends AbstractEntity {

    @NonNull
    @Enumerated(EnumType.STRING)
    PermissionEnum name;

    @Column(columnDefinition = "MEDIUMTEXT")
    String description;

    @OneToMany(mappedBy = RolePermission_.PERMISSION)
    Set<RolePermission> rolePermissions;
}
