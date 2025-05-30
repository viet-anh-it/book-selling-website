package io.github.viet_anh_it.book_selling_website.model;

import java.time.LocalDate;
import java.util.Set;

import io.github.viet_anh_it.book_selling_website.enums.GenderEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "users")
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

    @JoinColumn(name = "role_id")
    @ManyToOne
    Role role;

    @Builder.Default
    @OneToOne(mappedBy = Address_.USER, cascade = CascadeType.PERSIST)
    Address address = new Address();

    @OneToMany(mappedBy = Review_.USER)
    Set<Review> reviews;

    @Builder.Default
    @OneToOne(mappedBy = Cart_.USER, cascade = CascadeType.PERSIST)
    Cart cart = new Cart();

    @OneToMany(mappedBy = Order_.USER)
    Set<Order> orders;

    @OneToMany(mappedBy = RefreshToken_.USER)
    Set<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = BlackListedAccessToken_.USER)
    Set<BlackListedAccessToken> blackListedAccessTokens;

    @OneToOne(mappedBy = VerificationToken_.USER)
    VerificationToken verificationToken;

    public void setCart(Cart cart) {
        this.cart = cart;
        this.cart.setUser(this);
    }

    public void setAddress(Address address) {
        this.address = address;
        this.address.setUser(this);
    }
}
