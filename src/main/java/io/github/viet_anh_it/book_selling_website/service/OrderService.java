package io.github.viet_anh_it.book_selling_website.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import io.github.viet_anh_it.book_selling_website.dto.OrderDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;

public interface OrderService {

    SuccessResponse<OrderDTO> createOrder(OrderDTO orderDTO);

    SuccessResponse<List<OrderDTO>> getAllOrders(Pageable pageable, Optional<String> optKeywordParam, Optional<String> optStatusParam);

    SuccessResponse<OrderDTO> getOrderById(long orderId);
}
