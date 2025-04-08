package io.github.viet_anh_it.book_selling_website.model;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends AbstractEntity {

    String isbn;
    String image;
    String publisherName;
    String translatorName;
    String name;
    int price;
    int discounted_price;
    int discounted_percentage;
    int sold_quantity;
    int stock_quantity;
    LocalDate publicationDate;
    String language;
    String dimension;
    float weight;
    int pageCount;
    String coverType;
    String recommendedAgeLabel;

    @Column(columnDefinition = "MEDIUMTEXT")
    String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = AuthorProduct_.PRODUCT)
    Set<AuthorProduct> authorProducts;

    @OneToOne
    @JoinColumn(name = "review_stat_id")
    ReviewStat reviewStat;

    @OneToMany(mappedBy = Review_.PRODUCT)
    Set<Review> reviews;

    @OneToMany(mappedBy = CartItem_.PRODUCT)
    Set<CartItem> cartItems;

    @OneToMany(mappedBy = OrderItem_.PRODUCT)
    Set<OrderItem> orderItems;
}
