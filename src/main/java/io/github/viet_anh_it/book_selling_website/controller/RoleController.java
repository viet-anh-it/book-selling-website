package io.github.viet_anh_it.book_selling_website.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.viet_anh_it.book_selling_website.dto.response.ReadAllRolesResponseDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @ResponseBody
    @GetMapping("/roles")
    public ResponseEntity<SuccessResponse<Set<ReadAllRolesResponseDTO>>> readAllRoles() {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(this.roleService.readAllRoles());
    }
}
