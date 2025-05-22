package io.github.viet_anh_it.book_selling_website.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles_permissions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermission extends AbstractEntity {

    @JoinColumn(name = "role_id")
    @ManyToOne
    Role role;

    @JoinColumn(name = "permission_id")
    @ManyToOne
    Permission permission;
}
