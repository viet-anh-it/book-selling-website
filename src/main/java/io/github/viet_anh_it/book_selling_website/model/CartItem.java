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
@Table(name = "cart_items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem extends AbstractEntity {

    String image;
    String name;
    int price;
    int quantity;
    int totalPrice;

    @JoinColumn(name = "cart_id")
    @ManyToOne
    Cart cart;

    @JoinColumn(name = "book_id")
    @ManyToOne
    Book book;
}
