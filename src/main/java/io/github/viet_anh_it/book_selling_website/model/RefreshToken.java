package io.github.viet_anh_it.book_selling_website.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken extends AbstractEntity {

    @Column(columnDefinition = "TEXT")
    String tokenValue;

    String jti;
    Instant expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
