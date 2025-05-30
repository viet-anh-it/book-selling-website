package io.github.viet_anh_it.book_selling_website.model;

import java.time.LocalDateTime;
import java.util.Set;

import io.github.viet_anh_it.book_selling_website.enums.OrderStatusEnum;
import io.github.viet_anh_it.book_selling_website.enums.PaymentMethodEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends AbstractEntity {

    String code;
    OrderStatusEnum status;
    LocalDateTime at;
    String name;
    String phone;
    String province;
    String district;
    String ward;
    String home;
    PaymentMethodEnum paymentMethod;
    int total;
    String note;

    @JoinColumn(name = "user_id")
    @ManyToOne
    User user;

    @OneToMany(mappedBy = OrderItem_.ORDER)
    Set<OrderItem> orderItems;
}
