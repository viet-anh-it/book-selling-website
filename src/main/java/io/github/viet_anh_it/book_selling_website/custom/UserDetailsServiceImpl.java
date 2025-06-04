package io.github.viet_anh_it.book_selling_website.custom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.github.viet_anh_it.book_selling_website.model.Permission;
import io.github.viet_anh_it.book_selling_website.model.Role;
import io.github.viet_anh_it.book_selling_website.service.PermissionService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    UserService userService;
    PermissionService permissionService;

    @NonFinal
    @Setter
    String defaultRolePrefix = "ROLE_";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        io.github.viet_anh_it.book_selling_website.model.User user = this.userService.findByEmail(username)
                .orElseThrow((() -> new UsernameNotFoundException("Thông tin xác thực không chính xác!")));

        Collection<GrantedAuthority> authorities = new HashSet<>();

        Role roleEntity = user.getRole();
        String roleName = this.defaultRolePrefix + roleEntity.getName().name();
        SimpleGrantedAuthority roleSimpleGrantedAuthority = new SimpleGrantedAuthority(roleName);
        authorities.add(roleSimpleGrantedAuthority);

        Optional<Set<Permission>> optionalPermissionSet = this.permissionService.findByRoleId(roleEntity.getId());
        if (optionalPermissionSet.isPresent()) {
            Set<Permission> permissionSet = optionalPermissionSet.get();
            Set<SimpleGrantedAuthority> permissionSimpleGrantedAuthoritySet = permissionSet.stream()
                    .map(permissionEntity -> new SimpleGrantedAuthority(permissionEntity.getName().name()))
                    .collect(Collectors.toSet());
            authorities.addAll(permissionSimpleGrantedAuthoritySet);
        }

        boolean enabled = user.isActive();
        boolean accountNonExpired = true; // Spring default value
        boolean credentialsNonExpired = true; // Spring default value
        boolean accountNonLocked = user.isLocked();

        return new User(user.getEmail(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
    }

}