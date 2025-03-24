package io.github.viet_anh_it.book_selling_website.model;

import java.time.LocalDate;
import java.util.Set;

import io.github.viet_anh_it.book_selling_website.enums.GenderEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AbstractEntity {

    String email;
    String password;
    String avatar;
    String name;
    LocalDate dob;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    String phone;
    boolean active;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    @OneToMany(mappedBy = Address_.USER)
    Set<Address> addresses;

    @OneToMany(mappedBy = Review_.USER)
    Set<Review> reviews;

    @OneToOne(mappedBy = Cart_.USER)
    Cart cart;

    @OneToMany(mappedBy = Order_.USER)
    Set<Order> orders;
}
