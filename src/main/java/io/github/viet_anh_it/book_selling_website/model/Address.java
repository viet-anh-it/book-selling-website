package io.github.viet_anh_it.book_selling_website.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
@Table(name = "addresses")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address extends AbstractEntity {

    @Builder.Default
    String province = "";

    @Builder.Default
    String district = "";

    @Builder.Default
    String ward = "";

    @Builder.Default
    String street = "";

    @Builder.Default
    boolean isDefault = false;

    @JoinColumn(name = "user_id")
    @OneToOne
    User user;
}
