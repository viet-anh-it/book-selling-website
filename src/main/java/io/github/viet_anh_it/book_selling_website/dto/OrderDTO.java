package io.github.viet_anh_it.book_selling_website.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.github.viet_anh_it.book_selling_website.enums.OrderStatusEnum;
import io.github.viet_anh_it.book_selling_website.enums.PaymentMethodEnum;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.First;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.Second;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO implements Serializable {

    Long id;
    OrderStatusEnum status;
    String code;
    LocalDateTime orderedAt;

    @NotBlank(message = "Họ tên người nhận không được trống!", groups = { First.class })
    String name;

    @NotBlank(message = "Số điện thoại người nhận không được trống!", groups = { First.class })
    @Pattern(regexp = "^0(3[2-9]|5[6,8,9]|7[0,6-9]|8[1-5]|9[0-9])[0-9]{7}$", message = "Số điện thoại không hợp lệ!", groups = { Second.class })
    String phone;

    @NotBlank(message = "Tỉnh/thành phố không được trống!", groups = { First.class })
    String province;

    @NotBlank(message = "Quận/huyện không được trống!", groups = { First.class })
    String district;

    @NotBlank(message = "Xã/phường không được trống!", groups = { First.class })
    String ward;

    @NotBlank(message = "Địa chỉ nhận hàng không được trống!", groups = { First.class })
    String home;

    String note;
    List<OrderDTO.OrderItem> orderItems;
    Integer total;
    PaymentMethodEnum paymentMethod;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderItem {
        Long cartItemId;
        Long bookId;
        String image;
        String name;
        Integer price;
        Integer quantity;
        Integer total;
    }
}
