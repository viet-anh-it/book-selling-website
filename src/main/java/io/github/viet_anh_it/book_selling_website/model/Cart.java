package io.github.viet_anh_it.book_selling_website.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "carts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart extends AbstractEntity {

    int uniqueProductCount;
    int totalPrice;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    @Builder.Default
    @OneToMany(mappedBy = CartItem_.CART)
    Set<CartItem> cartItems = new HashSet<>();
}
