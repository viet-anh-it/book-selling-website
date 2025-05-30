package io.github.viet_anh_it.book_selling_website.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.viet_anh_it.book_selling_website.dto.response.ReadAllRolesResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleRestController {

    RoleService roleService;

    @Secured({ "ROLE_ADMIN" })
    @GetMapping("/roles")
    public ResponseEntity<SuccessResponse<Set<ReadAllRolesResponseDTO>>> readAllRoles() {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(this.roleService.readAllRoles());
    }
}
