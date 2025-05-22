package io.github.viet_anh_it.book_selling_website.service.impl;

import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.model.ReviewStat;
import io.github.viet_anh_it.book_selling_website.repository.ReviewStatRepository;
import io.github.viet_anh_it.book_selling_website.service.ReviewStatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewStatServiceImpl implements ReviewStatService {

    ReviewStatRepository reviewStatRepository;

    @Override
    public ReviewStat save(ReviewStat reviewStat) {
        return this.reviewStatRepository.save(reviewStat);
    }
}
