package io.github.viet_anh_it.book_selling_website.model;

import java.time.Instant;

import io.github.viet_anh_it.book_selling_website.enums.TokenTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "verification_tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationToken extends AbstractEntity {

    String tokenValue;
    Instant expiresAt;
    // Instant usedAt;
    // boolean used;

    @Enumerated(EnumType.STRING)
    TokenTypeEnum type;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
}
