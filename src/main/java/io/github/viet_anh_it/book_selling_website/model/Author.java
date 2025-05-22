// package io.github.viet_anh_it.book_selling_website.model;

// import java.util.Set;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.OneToMany;
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
// @Table(name = "authors")
// @FieldDefaults(level = AccessLevel.PRIVATE)
// public class Author extends AbstractEntity {

// String avatar;
// String name;

// @Column(columnDefinition = "MEDIUMTEXT")
// String biography;

// @OneToMany(mappedBy = AuthorProduct_.AUTHOR)
// Set<AuthorProduct> authorProducts;
// }
