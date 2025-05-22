package io.github.viet_anh_it.book_selling_website.model;

import java.util.HashMap;
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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "review_stats")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewStat extends AbstractEntity {

    int totalReviews = 0;
    int totalRatingPoint = 0;
    int averageRatingPoint = 0;

    @ElementCollection
    @MapKeyColumn(name = "rating_point") // tên cột lưu key
    @Column(name = "count") // tên cột lưu value
    @CollectionTable(name = "count_by_rating_point", // tên bảng phụ
            joinColumns = @JoinColumn(name = "review_stat_id") // tên cột FK trong bảng phụ tham chiểu đến bảng
                                                               // review_stats
    )
    Map<Integer, Integer> countByRatingPoint = new HashMap<>();

    @ElementCollection
    @MapKeyColumn(name = "rating_point")
    @Column(name = "percentage")
    @CollectionTable(name = "percentage_by_rating_point", joinColumns = @JoinColumn(name = "review_stat_id"))
    Map<Integer, Integer> percentageByRatingPoint = new HashMap<>();

    @OneToOne(mappedBy = Book_.REVIEW_STAT)
    Book book;

    public ReviewStat() {
        for (int i = 1; i <= 5; i++) {
            this.countByRatingPoint.put(i, 0);
            // this.percentageByRatingPoint.put(i, 0);
        }
    }
}
