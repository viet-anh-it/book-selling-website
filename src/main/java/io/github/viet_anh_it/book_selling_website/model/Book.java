package io.github.viet_anh_it.book_selling_website.model;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CascadeType;
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
@Table(name = "books")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book extends AbstractEntity {

    String isbn;
    String name;
    String authorName;
    String publisherName;
    int price;
    String image;
    String translatorName;
    int discountedPrice;
    int discountedPercentage;
    int soldQuantity;
    int stockQuantity;
    LocalDate publicationDate;
    String language;
    String dimension;
    float weight;
    int pageCount;
    String coverType;
    String recommendedAgeLabel;

    @Column(columnDefinition = "MEDIUMTEXT")
    String description;

    @JoinColumn(name = "category_id")
    @ManyToOne
    Category category;

    // @OneToMany(mappedBy = AuthorProduct_.PRODUCT)
    // Set<AuthorProduct> authorProducts;

    @Builder.Default
    @JoinColumn(name = "review_stat_id")
    @OneToOne(cascade = { CascadeType.PERSIST })
    ReviewStat reviewStat = new ReviewStat();

    @OneToMany(mappedBy = Review_.BOOK)
    Set<Review> reviews;

    @OneToMany(mappedBy = CartItem_.PRODUCT)
    Set<CartItem> cartItems;

    @OneToMany(mappedBy = OrderItem_.PRODUCT)
    Set<OrderItem> orderItems;
}
