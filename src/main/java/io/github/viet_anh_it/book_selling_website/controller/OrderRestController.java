package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.viet_anh_it.book_selling_website.dto.OrderDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Order_;
import io.github.viet_anh_it.book_selling_website.service.OrderService;
import io.github.viet_anh_it.book_selling_website.validator.annotation.group.order.ValidationOrder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderRestController {

    OrderService orderService;

    @Secured({ "ROLE_CUSTOMER" })
    @PostMapping("/orders")
    public ResponseEntity<SuccessResponse<OrderDTO>> createOrder(@RequestBody(required = true) @Validated(value = { ValidationOrder.class }) OrderDTO orderDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.createOrder(orderDTO));
    }

    @Secured({ "ROLE_MANAGER" })
    @GetMapping("/orders")
    public ResponseEntity<SuccessResponse<List<OrderDTO>>> getAllOrders(
            @PageableDefault(page = 0, size = 6, sort = Order_.ORDERED_AT, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(name = "keyword") Optional<String> optKeywordParam,
            @RequestParam(name = "status") Optional<String> optStatusParam) {
        return ResponseEntity.status(HttpStatus.OK).body(this.orderService.getAllOrders(pageable, optKeywordParam, optStatusParam));
    }

    @Secured({ "ROLE_MANAGER" })
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<SuccessResponse<OrderDTO>> getOrderById(@PathVariable long orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.orderService.getOrderById(orderId));
    }
}
