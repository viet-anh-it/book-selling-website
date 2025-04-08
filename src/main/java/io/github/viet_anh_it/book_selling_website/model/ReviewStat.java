package io.github.viet_anh_it.book_selling_website.model;

import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
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
@Table(name = "review_stats")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewStat extends AbstractEntity {

    int totalReview;
    float averageRating;

    @ElementCollection
    @MapKeyColumn(name = "rating") // tên cột lưu key
    @Column(name = "count") // tên cột lưu value
    @CollectionTable(name = "review_stat_counts", joinColumns = @JoinColumn(name = "review_stat_id"))
    Map<Integer, Integer> reviewStatCounts;

    @ElementCollection
    @MapKeyColumn(name = "rating") // tên cột lưu key
    @Column(name = "percentage") // tên cột lưu value
    @CollectionTable(name = "review_stat_percentages", joinColumns = @JoinColumn(name = "review_stat_id"))
    Map<Integer, Integer> reviewStatPercentages;

    @OneToOne(mappedBy = Product_.REVIEW_STAT)
    Product product;
}
