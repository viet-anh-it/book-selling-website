package io.github.viet_anh_it.book_selling_website;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.viet_anh_it.book_selling_website.enums.PermissionEnum;
import io.github.viet_anh_it.book_selling_website.enums.RoleEnum;
import io.github.viet_anh_it.book_selling_website.model.Permission;
import io.github.viet_anh_it.book_selling_website.model.Role;
import io.github.viet_anh_it.book_selling_website.model.RolePermission;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.service.PermissionService;
import io.github.viet_anh_it.book_selling_website.service.RolePermissionService;
import io.github.viet_anh_it.book_selling_website.service.RoleService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@SpringBootApplication
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookSellingWebsiteApplication {

	UserService userService;
	PasswordEncoder passwordEncoder;
	RoleService roleService;
	PermissionService permissionService;
	RolePermissionService rolePermissionService;

	public static void main(String[] args) {
		SpringApplication.run(BookSellingWebsiteApplication.class, args);
	}

	@Bean
	public CommandLineRunner roleAndPermissionDataInitializer() {
		return args -> {

			Map<RoleEnum, Role> roleMap = new HashMap<>();
			Arrays.stream(RoleEnum.values()).forEach(roleEnum -> {
				if (!this.roleService.existsByName(roleEnum)) {
					Role role = this.roleService.save(new Role(roleEnum));
					roleMap.put(roleEnum, role);
				} else {
					Role role = this.roleService.findByName(roleEnum).get();
					roleMap.put(roleEnum, role);
				}
			});

			Map<PermissionEnum, Permission> permissionMap = new HashMap<>();
			Arrays.stream(PermissionEnum.values()).forEach(permissionEnum -> {
				if (!this.permissionService.existsByName(permissionEnum)) {
					Permission permission = this.permissionService.save(new Permission(permissionEnum));
					permissionMap.put(permissionEnum, permission);
				} else {
					Permission permission = this.permissionService.findByName(permissionEnum).get();
					permissionMap.put(permissionEnum, permission);
				}
			});

			Set<RolePermission> rolePermissions = new HashSet<>(Arrays.asList(
					new RolePermission(roleMap.get(RoleEnum.ADMIN), permissionMap.get(PermissionEnum.ASSIGN_PERMISSION_TO_ROLE)),
					new RolePermission(roleMap.get(RoleEnum.ADMIN), permissionMap.get(PermissionEnum.ASSIGN_ROLE_TO_ACCOUNT)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.UNLOCK_ACCOUNT)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.LOCK_ACCOUNT)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.CREATE_BOOK)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.UPDATE_BOOK)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.DELETE_BOOK)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.APPROVE_REVIEW)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.DELETE_REVIEW)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.CREATE_CATEGORY)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.GET_CATEGORY)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.UPDATE_CATEGORY)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.DELETE_CATEGORY)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.GET_ORDER)),
					new RolePermission(roleMap.get(RoleEnum.MANAGER), permissionMap.get(PermissionEnum.GET_REVIEW)),
					new RolePermission(roleMap.get(RoleEnum.STAFF), permissionMap.get(PermissionEnum.UPDATE_ORDER_STATUS)),
					new RolePermission(roleMap.get(RoleEnum.CUSTOMER), permissionMap.get(PermissionEnum.POST_REVIEW)),
					new RolePermission(roleMap.get(RoleEnum.CUSTOMER), permissionMap.get(PermissionEnum.ADD_TO_CART)),
					new RolePermission(roleMap.get(RoleEnum.CUSTOMER), permissionMap.get(PermissionEnum.DELETE_CART_ITEM)),
					new RolePermission(roleMap.get(RoleEnum.CUSTOMER), permissionMap.get(PermissionEnum.CREATE_ORDER)),
					new RolePermission(roleMap.get(RoleEnum.CUSTOMER), permissionMap.get(PermissionEnum.DELETE_REVIEW)),
					new RolePermission(roleMap.get(RoleEnum.CUSTOMER), permissionMap.get(PermissionEnum.CANCEL_ORDER))));

			rolePermissions.stream()
					.filter(rolePermissionEntity -> !this.rolePermissionService
							.existsByRoleAndPermission(rolePermissionEntity.getRole(), rolePermissionEntity.getPermission()))
					.forEach(rolePermissionEntity -> this.rolePermissionService.save(rolePermissionEntity));

			if (!this.userService.existsByEmail("admin@email.admin")) {
				User admin = User.builder()
						.email("admin@email.admin")
						.password(this.passwordEncoder.encode("admin123"))
						.role(roleMap.get(RoleEnum.ADMIN))
						.active(true)
						.build();
				this.userService.save(admin);
			}
		};
	}
}
