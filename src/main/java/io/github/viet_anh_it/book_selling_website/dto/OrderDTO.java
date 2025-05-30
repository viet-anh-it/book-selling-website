package io.github.viet_anh_it.book_selling_website.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO {

    LocalDateTime at;
    String name;
    String phone;
    String province;
    String district;
    String ward;
    String street;
    String note;
    List<OrderDTO.OrderItem> orderItems;
    Integer total;

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderItem {
        String image;
        String name;
        Integer price;
        Integer quantity;
        Integer total;
    }
}
