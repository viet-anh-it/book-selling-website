package io.github.viet_anh_it.book_selling_website.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    // @CreatedDate
    // LocalDateTime created_at;

    // @CreatedBy
    // String created_by;

    // @LastModifiedDate
    // LocalDateTime updated_at;

    // @LastModifiedBy
    // String updated_by;
}
