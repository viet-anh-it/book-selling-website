package io.github.viet_anh_it.book_selling_website.converter;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import io.github.viet_anh_it.book_selling_website.dto.BookPriceRangeDTO;

public class PriceQueryParamConverter implements Converter<String, BookPriceRangeDTO> {

    @Override
    @Nullable
    public BookPriceRangeDTO convert(@NonNull String priceParamValuePart) {
        String[] values = priceParamValuePart.split(",");
        List<Integer> integerList = Arrays.stream(values)
                .filter(value -> value.matches("\\d+"))
                .map(value -> Integer.valueOf(value))
                .toList();
        if (values.length != 2) {
            return null;
        } else if (integerList.size() != 2) {
            return null;
        } else if (!(integerList.getFirst() < integerList.getLast())) {
            return null;
        } else {
            return BookPriceRangeDTO.builder()
                    .minPrice(integerList.getFirst())
                    .maxPrice(integerList.getLast())
                    .build();
        }
    }
}
