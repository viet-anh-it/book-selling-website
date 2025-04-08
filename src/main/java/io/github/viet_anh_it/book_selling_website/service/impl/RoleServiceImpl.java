package io.github.viet_anh_it.book_selling_website.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.dto.response.ReadAllRolesResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.enums.RoleEnum;
import io.github.viet_anh_it.book_selling_website.model.Role;
import io.github.viet_anh_it.book_selling_website.repository.RoleRepository;
import io.github.viet_anh_it.book_selling_website.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(RoleEnum roleName) {
        return this.roleRepository.findByName(roleName);
    }

    @Override
    public Role save(Role roleEntity) {
        return this.roleRepository.save(roleEntity);
    }

    @Override
    public boolean existsByName(RoleEnum roleName) {
        return this.roleRepository.existsByName(roleName);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SuccessResponse<Set<ReadAllRolesResponseDTO>> readAllRoles() {
        List<Role> roleList = this.roleRepository.findAll();

        Set<ReadAllRolesResponseDTO> readAllRolesResponseDTOSet = roleList.stream()
                .map(roleEntity -> ReadAllRolesResponseDTO.builder()
                        .id(roleEntity.getId())
                        .name(roleEntity.getName().name())
                        .description(roleEntity.getDescription())
                        .build())
                .collect(Collectors.toSet());

        SuccessResponse<Set<ReadAllRolesResponseDTO>> successResponse = SuccessResponse
                .<Set<ReadAllRolesResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách vai trò thành công!")
                .data(readAllRolesResponseDTOSet)
                .build();

        return successResponse;
    }
}
