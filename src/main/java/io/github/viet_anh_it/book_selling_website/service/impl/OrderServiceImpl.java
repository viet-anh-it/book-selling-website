package io.github.viet_anh_it.book_selling_website.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.viet_anh_it.book_selling_website.dto.OrderDTO;
import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.enums.OrderStatusEnum;
import io.github.viet_anh_it.book_selling_website.enums.PaymentMethodEnum;
import io.github.viet_anh_it.book_selling_website.model.Book;
import io.github.viet_anh_it.book_selling_website.model.Cart;
import io.github.viet_anh_it.book_selling_website.model.Order;
import io.github.viet_anh_it.book_selling_website.model.OrderItem;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.repository.BookRepository;
import io.github.viet_anh_it.book_selling_website.repository.CartItemRepository;
import io.github.viet_anh_it.book_selling_website.repository.CartRepository;
import io.github.viet_anh_it.book_selling_website.repository.OrderRepository;
import io.github.viet_anh_it.book_selling_website.service.EmailService;
import io.github.viet_anh_it.book_selling_website.service.OrderService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import io.github.viet_anh_it.book_selling_website.specification.OrderSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

        EmailService emailService;
        UserService userService;
        CartRepository cartRepository;
        BookRepository bookRepository;
        OrderRepository orderRepository;
        CartItemRepository cartItemRepository;

        @Override
        @Transactional
        @PreAuthorize("hasAuthority('CREATE_ORDER')")
        public SuccessResponse<OrderDTO> createOrder(OrderDTO orderDTO) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User currentLoggedInUser = this.userService.findByEmail(username).get();
                Order order = new Order();
                order.setUser(currentLoggedInUser);
                order.setOrderedAt(LocalDateTime.now());
                order.setStatus(OrderStatusEnum.PENDING);
                order.setName(orderDTO.getName());
                order.setPhone(orderDTO.getPhone());
                order.setProvince(orderDTO.getProvince());
                order.setDistrict(orderDTO.getDistrict());
                order.setWard(orderDTO.getWard());
                order.setHome(orderDTO.getHome());
                order.setNote(orderDTO.getNote());
                order.setTotal(orderDTO.getTotal());
                order.setPaymentMethod(PaymentMethodEnum.COD);
                List<Long> cartItemIds = orderDTO.getOrderItems().stream()
                                .map(orderDtoItem -> {
                                        OrderItem orderItem = OrderItem.builder()
                                                        .image(orderDtoItem.getImage())
                                                        .name(orderDtoItem.getName())
                                                        .price(orderDtoItem.getPrice())
                                                        .quantity(orderDtoItem.getQuantity())
                                                        .total(orderDtoItem.getTotal())
                                                        .build();
                                        orderItem.setOrder(order);
                                        Book book = this.bookRepository.findById(orderDtoItem.getBookId()).get();
                                        orderItem.setBook(book);
                                        return orderDtoItem.getCartItemId();
                                })
                                .toList();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
                String timestamp = LocalDateTime.now().format(formatter);
                int randomSuffix = new Random().nextInt(9000) + 1000; // Số ngẫu nhiên 4 chữ số (1000-9999)
                order.setCode("ORD" + timestamp + randomSuffix); // Ví dụ: ORD2405311430151234
                this.orderRepository.save(order);
                this.cartItemRepository.deleteAllById(cartItemIds);
                Cart cart = currentLoggedInUser.getCart();
                cart.setTotalPrice(0);
                cart.setUniqueProductCount(0);
                this.cartRepository.save(cart);
                OrderDTO responseOrderDto = OrderDTO.builder()
                                .code(order.getCode())
                                .orderedAt(order.getOrderedAt())
                                .build();
                return SuccessResponse.<OrderDTO>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Tạo đơn hàng thành công!")
                                .data(responseOrderDto)
                                .build();
        }

        @Override
        @PreAuthorize("hasAuthority('GET_ORDER')")
        public SuccessResponse<List<OrderDTO>> getAllOrders(Pageable pageable, Optional<String> optKeywordParam, Optional<String> optStatusParam) {
                Specification<Order> combinedSpec = Specification.where(null);
                if (optKeywordParam.isPresent()) {
                        String keyword = optKeywordParam.get();
                        if (!keyword.trim().equals("")) {
                                Specification<Order> hasKeyword = OrderSpecification.hasKeyword(keyword);
                                combinedSpec = combinedSpec.and(hasKeyword);
                        }
                }

                if (optStatusParam.isPresent()) {
                        String status = optStatusParam.get();
                        if (!status.trim().equals("")) {
                                Specification<Order> hasStatus = OrderSpecification.hasStatus(status);
                                combinedSpec = combinedSpec.and(hasStatus);
                        }
                }

                Page<Order> orderPage = this.orderRepository.findAll(combinedSpec, pageable);
                List<OrderDTO> orderDtos = orderPage.getContent().stream()
                                .map(order -> OrderDTO.builder()
                                                .id(order.getId())
                                                .code(order.getCode())
                                                .name(order.getName())
                                                .phone(order.getPhone())
                                                .province(order.getProvince())
                                                .district(order.getDistrict())
                                                .ward(order.getWard())
                                                .home(order.getHome())
                                                .orderedAt(order.getOrderedAt())
                                                .total(order.getTotal())
                                                .paymentMethod(order.getPaymentMethod())
                                                .status(order.getStatus())
                                                .build())
                                .toList();
                PaginationMetadataDTO paginationMetadata = PaginationMetadataDTO.builder()
                                .currentPosition(orderPage.getNumber())
                                .numberOfElementsPerPage(orderPage.getSize())
                                .totalNumberOfElements(orderPage.getTotalElements())
                                .totalNumberOfPages(orderPage.getTotalPages())
                                .hasNextPage(orderPage.hasNext())
                                .hasPreviousPage(orderPage.hasPrevious())
                                .nextPosition(orderPage.hasNext() ? orderPage.getNumber() + 1 : -1)
                                .previousPosition(orderPage.hasPrevious() ? orderPage.getNumber() - 1 : -1)
                                .build();
                return SuccessResponse.<List<OrderDTO>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Lấy tất cả đơn hàng thành công!")
                                .data(orderDtos)
                                .paginationMetadata(paginationMetadata)
                                .build();
        }

        @Override
        @PreAuthorize("hasAuthority('GET_ORDER')")
        public SuccessResponse<OrderDTO> getOrderById(long orderId) {
                Optional<Order> optOrder = this.orderRepository.findById(orderId);
                SuccessResponse<OrderDTO> successResponse = SuccessResponse.<OrderDTO>builder()
                                .status(HttpStatus.OK.value())
                                .message("Lấy đơn hàng thành công!")
                                .build();
                if (optOrder.isPresent()) {
                        Order order = optOrder.get();
                        OrderDTO orderDto = OrderDTO.builder()
                                        .id(order.getId())
                                        .code(order.getCode())
                                        .name(order.getName())
                                        .phone(order.getPhone())
                                        .province(order.getProvince())
                                        .district(order.getDistrict())
                                        .ward(order.getWard())
                                        .home(order.getHome())
                                        .orderedAt(order.getOrderedAt())
                                        .total(order.getTotal())
                                        .paymentMethod(order.getPaymentMethod())
                                        .status(order.getStatus())
                                        .note(order.getNote())
                                        .build();
                        List<OrderDTO.OrderItem> orderDtoItems = order.getOrderItems().stream()
                                        .map(orderItem -> OrderDTO.OrderItem.builder()
                                                        .image(orderItem.getImage())
                                                        .name(orderItem.getName())
                                                        .price(orderItem.getPrice())
                                                        .quantity(orderItem.getQuantity())
                                                        .total(orderItem.getTotal())
                                                        .build())
                                        .toList();
                        orderDto.setOrderItems(orderDtoItems);
                        successResponse.setData(orderDto);
                        return successResponse;
                }
                return successResponse;
        }

        @Override
        @Transactional
        @PreAuthorize("hasAuthority('APPROVE_ORDER')")
        public SuccessResponse<Void> approveOrder(long orderId) {
                Optional<Order> optOrder = this.orderRepository.findById(orderId);
                if (optOrder.isPresent()) {
                        Order order = optOrder.get();
                        order.setStatus(OrderStatusEnum.APPROVED);
                        this.orderRepository.save(order);
                        order.getOrderItems().forEach(orderItem -> {
                                Book book = orderItem.getBook();
                                book.setSoldQuantity(book.getSoldQuantity() + orderItem.getQuantity());
                                this.bookRepository.save(book);
                        });
                        this.emailService.sendOrderApprovalEmail(order.getUser());
                }
                return SuccessResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Duyệt đơn hàng thành công!")
                                .build();
        }

        @Override
        @Transactional
        @PreAuthorize("hasAuthority('REJECT_ORDER')")
        public SuccessResponse<Void> rejectOrder(long orderId) {
                Optional<Order> optOrder = this.orderRepository.findById(orderId);
                if (optOrder.isPresent()) {
                        Order order = optOrder.get();
                        order.setStatus(OrderStatusEnum.REJECTED);
                        this.orderRepository.save(order);
                        order.getOrderItems().forEach(orderItem -> {
                                Book book = orderItem.getBook();
                                book.setStockQuantity(book.getStockQuantity() + orderItem.getQuantity());
                                this.bookRepository.save(book);
                        });
                        this.emailService.sendOrderRejectionEmail(order.getUser());
                }
                return SuccessResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Từ chối đơn hàng thành công!")
                                .build();
        }

        @Override
        @Transactional
        @PreAuthorize("hasAuthority('UPDATE_ORDER_STATUS')")
        public SuccessResponse<Void> updateOrderStatus(long orderId, Optional<String> optStatusParam) {
                Optional<Order> optOrder = this.orderRepository.findById(orderId);
                if (optOrder.isPresent() && optStatusParam.isPresent()) {
                        Order order = optOrder.get();
                        String status = optStatusParam.get();
                        Arrays.stream(OrderStatusEnum.values())
                                        .filter(orderStatus -> orderStatus.name().equals(status))
                                        .findFirst()
                                        .ifPresent(orderStatus -> {
                                                order.setStatus(orderStatus);
                                                this.orderRepository.save(order);
                                        });
                        this.emailService.sendUpdateOrderStatusEmail(order.getUser(), order.getStatus());
                }
                return SuccessResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Cập nhật trạng thái đơn hàng thành công!")
                                .build();
        }

        @Override
        @PreAuthorize("hasAuthority('GET_PERSONAL_ORDER')")
        public SuccessResponse<List<OrderDTO>> getPersonalOrders(Pageable pageable, Optional<String> optKeywordParam, Optional<String> optStatusParam) {
                Specification<Order> combinedSpec = Specification.where(null);
                if (optKeywordParam.isPresent()) {
                        String keyword = optKeywordParam.get();
                        if (!keyword.trim().equals("")) {
                                Specification<Order> hasKeyword = OrderSpecification.hasKeyword(keyword);
                                combinedSpec = combinedSpec.and(hasKeyword);
                        }
                }

                if (optStatusParam.isPresent()) {
                        String status = optStatusParam.get();
                        if (!status.trim().equals("")) {
                                Specification<Order> hasStatus = OrderSpecification.hasStatus(status);
                                combinedSpec = combinedSpec.and(hasStatus);
                        }
                }
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Page<Order> orderPage = this.orderRepository.getPersonalOrders(username, pageable, combinedSpec);
                List<Order> orders = orderPage.getContent();
                List<OrderDTO> orderDtos = orders.stream()
                                .map(order -> OrderDTO.builder()
                                                .id(order.getId())
                                                .code(order.getCode())
                                                .name(order.getName())
                                                .phone(order.getPhone())
                                                .province(order.getProvince())
                                                .district(order.getDistrict())
                                                .ward(order.getWard())
                                                .home(order.getHome())
                                                .orderedAt(order.getOrderedAt())
                                                .total(order.getTotal())
                                                .paymentMethod(order.getPaymentMethod())
                                                .status(order.getStatus())
                                                .build())
                                .toList();
                PaginationMetadataDTO paginationMetadata = PaginationMetadataDTO.builder()
                                .currentPosition(orderPage.getNumber())
                                .numberOfElementsPerPage(orderPage.getSize())
                                .totalNumberOfElements(orderPage.getTotalElements())
                                .totalNumberOfPages(orderPage.getTotalPages())
                                .hasNextPage(orderPage.hasNext())
                                .hasPreviousPage(orderPage.hasPrevious())
                                .nextPosition(orderPage.hasNext() ? orderPage.getNumber() + 1 : -1)
                                .previousPosition(orderPage.hasPrevious() ? orderPage.getNumber() - 1 : -1)
                                .build();
                return SuccessResponse.<List<OrderDTO>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Lấy đơn hàng thành công!")
                                .data(orderDtos)
                                .paginationMetadata(paginationMetadata)
                                .build();
        }

}
