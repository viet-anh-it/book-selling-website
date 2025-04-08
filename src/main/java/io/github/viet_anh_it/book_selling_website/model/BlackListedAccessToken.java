package io.github.viet_anh_it.book_selling_website.model;

import java.time.Instant;

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
@Table(name = "black_listed_access_tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlackListedAccessToken extends AbstractEntity {

    String jti;
    Instant expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
