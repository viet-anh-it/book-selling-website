package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.viet_anh_it.book_selling_website.dto.CategoryDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Category_;
import io.github.viet_anh_it.book_selling_website.service.CategoryService;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.ValidationOrder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryRestController {

        CategoryService categoryService;

        @Secured({ "ROLE_MANAGER" })
        @PostMapping("/categories")
        public ResponseEntity<?> createCategory(@RequestBody @Validated({ ValidationOrder.class }) CategoryDTO categoryDTO) {
                this.categoryService.createCategory(categoryDTO);
                SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Tạo danh mục thành công!")
                                .build();
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(successResponse);
        }

        @Secured({ "ROLE_MANAGER" })
        @GetMapping("/categories")
        public ResponseEntity<SuccessResponse<List<CategoryDTO>>> getAllCategories(@RequestParam(name = "name") Optional<String> optName,
                        @PageableDefault(page = 0, size = 6, sort = Category_.NAME, direction = Direction.ASC) Pageable pageable) {
                SuccessResponse<List<CategoryDTO>> successResponse = this.categoryService.getAllCategories(optName, pageable);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(successResponse);
        }

        @Secured({ "ROLE_MANAGER" })
        @GetMapping("/categories/{categoryId}")
        public ResponseEntity<SuccessResponse<CategoryDTO>> getCategoryById(@PathVariable("categoryId") Long categoryId) {
                Optional<CategoryDTO> optCategoryDto = this.categoryService.getCategoryById(categoryId);
                SuccessResponse<CategoryDTO> successResponse = SuccessResponse.<CategoryDTO>builder()
                                .status(HttpStatus.OK.value())
                                .message("Lấy danh mục thành công!")
                                .data(optCategoryDto.isPresent() ? optCategoryDto.get() : null)
                                .build();
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(successResponse);
        }

        @Secured({ "ROLE_MANAGER" })
        @PutMapping("/categories")
        public ResponseEntity<SuccessResponse<Void>> updateCategoryById(@RequestBody @Validated({ ValidationOrder.class }) CategoryDTO categoryDTO) {
                this.categoryService.updateCategory(categoryDTO);
                SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Cập nhật danh mục thành công!")
                                .build();
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(successResponse);
        }

        @Secured({ "ROLE_MANAGER" })
        @DeleteMapping("/categories/{categoryId}")
        public ResponseEntity<SuccessResponse<Void>> deleteCategoryById(@PathVariable(name = "categoryId") long categoryId) {
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(this.categoryService.deleteCategoryById(categoryId));
        }
}
