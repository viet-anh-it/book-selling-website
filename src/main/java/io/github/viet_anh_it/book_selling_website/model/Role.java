package io.github.viet_anh_it.book_selling_website.model;

import java.util.Set;

import io.github.viet_anh_it.book_selling_website.enums.RoleEnum;
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
@Table(name = "roles")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends AbstractEntity {

    @NonNull
    @Enumerated(EnumType.STRING)
    RoleEnum name;

    @Column(columnDefinition = "MEDIUMTEXT")
    String description;

    @OneToMany(mappedBy = RolePermission_.ROLE)
    Set<RolePermission> rolePermissions;

    @OneToMany(mappedBy = User_.ROLE)
    Set<User> users;
}
