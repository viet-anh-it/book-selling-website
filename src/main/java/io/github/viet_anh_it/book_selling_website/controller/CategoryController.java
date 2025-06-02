package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.viet_anh_it.book_selling_website.dto.CategoryDTO;
import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Category_;
import io.github.viet_anh_it.book_selling_website.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    static int PAGE = 0;
    static int SIZE = 6;
    static String PROPERTY = Category_.NAME;
    static Direction DIRECTION = Sort.Direction.ASC;
    static Sort SORT = Sort.by(DIRECTION, PROPERTY);
    static Pageable PAGEABLE = PageRequest.of(PAGE, SIZE, SORT);

    CategoryService categoryService;

    @Secured({ "ROLE_MANAGER" })
    @GetMapping("/manager/categories")
    public String getManageCategoryPage(Model model) {
        SuccessResponse<List<CategoryDTO>> successResponse = this.categoryService.getAllCategories(Optional.empty(), PAGEABLE);
        List<CategoryDTO> categoryDtoList = successResponse.getData();
        PaginationMetadataDTO paginationMetadata = successResponse.getPaginationMetadata();
        model.addAttribute("categories", categoryDtoList);
        model.addAttribute("paginationMetadata", paginationMetadata);
        return "manageCategories";
    }
}
