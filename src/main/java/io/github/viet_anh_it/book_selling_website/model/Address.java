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
@Table(name = "addresses")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address extends AbstractEntity {

    String province;
    String district;
    String avatar;
    String ward;
    String street;
    boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
