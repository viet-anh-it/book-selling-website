package io.github.viet_anh_it.book_selling_website.dto;

import java.io.Serializable;

import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.First;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoryDTO implements Serializable {

    long id;

    @NotBlank(message = "Tên danh mục không được trống!", groups = { First.class })
    String name;

    @NotBlank(message = "Mô tả danh mục không được trống!", groups = { First.class })
    String description;
}
