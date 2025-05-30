package io.github.viet_anh_it.book_selling_website.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import io.github.viet_anh_it.book_selling_website.dto.CategoryDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;

public interface CategoryService {

    void createCategory(CategoryDTO categoryDTO);

    SuccessResponse<List<CategoryDTO>> getAllCategories(String name, Pageable pageable);

    Optional<CategoryDTO> getCategoryById(Long categoryId);

    void updateCategory(CategoryDTO categoryDTO);

    SuccessResponse<Void> deleteCategoryById(Long categoryId);
}
