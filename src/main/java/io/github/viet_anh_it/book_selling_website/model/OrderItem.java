package io.github.viet_anh_it.book_selling_website.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "order_items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem extends AbstractEntity {

    String image;
    String name;
    int price;
    int quantity;
    int total;

    @JoinColumn(name = "order_id")
    @ManyToOne
    Order order;

    @JoinColumn(name = "book_id")
    @ManyToOne
    Book book;

    public void setOrder(Order order) {
        this.order = order;
        this.order.getOrderItems().add(this);
    }
}
