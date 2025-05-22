package io.github.viet_anh_it.book_selling_website.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
@Table(name = "reviews")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review extends AbstractEntity {

    String name;
    LocalDateTime postedAt;
    int rate;
    boolean approved;

    @Column(columnDefinition = "MEDIUMTEXT")
    String comment;

    @JoinColumn(name = "book_id")
    @ManyToOne
    Book book;

    @JoinColumn(name = "user_id")
    @ManyToOne
    User user;
}
