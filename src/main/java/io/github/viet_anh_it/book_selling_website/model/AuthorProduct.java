// package io.github.viet_anh_it.book_selling_website.model;

// import jakarta.persistence.Entity;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import lombok.AccessLevel;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import lombok.experimental.FieldDefaults;

// @Getter
// @Setter
// @Entity
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "authors_products")
// @FieldDefaults(level = AccessLevel.PRIVATE)
// public class AuthorProduct extends AbstractEntity {

// @ManyToOne
// @JoinColumn(name = "author_id")
// Author author;

// @ManyToOne
// @JoinColumn(name = "product_id")
// Book product;
// }
