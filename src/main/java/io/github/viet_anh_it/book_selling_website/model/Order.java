package io.github.viet_anh_it.book_selling_website.model;

import java.time.LocalDateTime;
import java.util.Set;

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
    String status;
    LocalDateTime orderedAt;
    String receiverName;
    String receiverPhone;
    String province;
    String district;
    String ward;
    String street;
    String paymentMethod;
    int totalPrice;
    String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = OrderItem_.ORDER)
    Set<OrderItem> orderItems;
}
