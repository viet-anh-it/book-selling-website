package io.github.viet_anh_it.book_selling_website.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.viet_anh_it.book_selling_website.dto.CategoryDTO;
import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Category;
import io.github.viet_anh_it.book_selling_website.repository.CategoryRepository;
import io.github.viet_anh_it.book_selling_website.service.CategoryService;
import io.github.viet_anh_it.book_selling_website.specification.CategorySpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('CREATE_CATEGORY')")
    public void createCategory(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .build();
        this.categoryRepository.save(category);
    }

    @Override
    @PreAuthorize("hasAuthority('GET_CATEGORY')")
    public SuccessResponse<List<CategoryDTO>> getAllCategories(Optional<String> optName, Pageable pageable) {
        Specification<Category> combinedSpec = Specification.where(null);
        if (optName.isPresent()) {
            String name = optName.get();
            Specification<Category> hasNameLike = CategorySpecification.hasNameLike(name);
            combinedSpec = combinedSpec.and(hasNameLike);
        }
        Page<Category> page = this.categoryRepository.findAll(combinedSpec, pageable);
        List<Category> categories = page.getContent();
        Stream<Category> stream = categories.stream();
        List<CategoryDTO> categoryDtoList = stream
                .map(category -> new CategoryDTO(category.getId(), category.getName(), category.getDescription()))
                .toList();
        PaginationMetadataDTO paginationMetadataDTO = PaginationMetadataDTO.builder()
                .currentPosition(page.getNumber())
                .numberOfElementsPerPage(page.getSize())
                .totalNumberOfElements(page.getTotalElements())
                .totalNumberOfPages(page.getTotalPages())
                .hasNextPage(page.hasNext())
                .hasPreviousPage(page.hasPrevious())
                .nextPosition(page.hasNext() ? page.getNumber() + 1 : -1)
                .previousPosition(page.hasPrevious() ? page.getNumber() - 1 : -1)
                .build();
        SuccessResponse<List<CategoryDTO>> successResponse = SuccessResponse.<List<CategoryDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh mục thành công!")
                .data(categoryDtoList)
                .paginationMetadata(paginationMetadataDTO)
                .build();
        return successResponse;
    }

    @Override
    @PreAuthorize("hasAuthority('GET_CATEGORY')")
    public Optional<CategoryDTO> getCategoryById(Long categoryId) {
        Optional<Category> optCategory = this.categoryRepository.findById(categoryId);
        if (optCategory.isPresent()) {
            Category category = optCategory.get();
            return Optional.of(new CategoryDTO(category.getId(), category.getName(), category.getDescription()));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('UPDATE_CATEGORY')")
    public void updateCategory(CategoryDTO categoryDTO) {
        Optional<Category> optCategory = this.categoryRepository.findById(categoryDTO.getId());
        if (optCategory.isPresent()) {
            Category category = optCategory.get();
            category.setName(categoryDTO.getName());
            category.setDescription(categoryDTO.getDescription());
            this.categoryRepository.save(category);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('DELETE_CATEGORY')")
    public SuccessResponse<Void> deleteCategoryById(Long categoryId) {
        this.categoryRepository.deleteById(categoryId);
        return SuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Xóa danh mục thành công!")
                .data(null)
                .build();
    }
}
