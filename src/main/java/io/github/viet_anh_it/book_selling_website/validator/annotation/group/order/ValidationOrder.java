package io.github.viet_anh_it.book_selling_website.validator.annotation.group.order;

import jakarta.validation.GroupSequence;

@GroupSequence({ First.class, Second.class, Third.class, Fourth.class })
public interface ValidationOrder {

}
