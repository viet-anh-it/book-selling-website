package io.github.viet_anh_it.book_selling_website.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.github.viet_anh_it.book_selling_website.dto.CartDTO;
import io.github.viet_anh_it.book_selling_website.dto.CartItemDTO;
import io.github.viet_anh_it.book_selling_website.dto.OrderDTO;
import io.github.viet_anh_it.book_selling_website.dto.PaginationMetadataDTO;
import io.github.viet_anh_it.book_selling_website.dto.response.SuccessResponse;
import io.github.viet_anh_it.book_selling_website.model.Order_;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.service.CartItemService;
import io.github.viet_anh_it.book_selling_website.service.CartService;
import io.github.viet_anh_it.book_selling_website.service.OrderService;
import io.github.viet_anh_it.book_selling_website.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    static String PROPERTY = Order_.ORDERED_AT;
    static Direction DIRECTION = Direction.ASC;
    static Sort SORT = Sort.by(DIRECTION, PROPERTY);
    static int PAGE = 0;
    static int SIZE = 6;
    static Pageable PAGEABLE = PageRequest.of(PAGE, SIZE, SORT);

    UserService userService;
    CartService cartService;
    OrderService orderService;
    CartItemService cartItemService;

    @Secured({ "ROLE_CUSTOMER" })
    @GetMapping("/orders/checkout")
    public String getCheckoutPage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentLoggedInUser = this.userService.findByEmail(username).get();
        model.addAttribute("user", currentLoggedInUser);
        List<CartItemDTO> cartItemDtos = this.cartItemService.getAllCartItems().getData();
        model.addAttribute("cartItems", cartItemDtos);
        CartDTO cartDto = this.cartService.getCart().getData();
        model.addAttribute("cart", cartDto);
        return "order";
    }

    @Secured({ "ROLE_CUSTOMER" })
    @GetMapping("/orders/success")
    public String getOrderSuccessPage() {
        return "orderSuccess";
    }

    @Secured({ "ROLE_MANAGER", "ROLE_STAFF" })
    @GetMapping("/manager/orders")
    public String getManageOrdersPage(Model model) {
        SuccessResponse<List<OrderDTO>> successResponse = this.orderService.getAllOrders(PAGEABLE, Optional.empty(), Optional.empty());
        List<OrderDTO> orderDtos = successResponse.getData();
        PaginationMetadataDTO paginationMetadata = successResponse.getPaginationMetadata();
        model.addAttribute("orders", orderDtos);
        model.addAttribute("paginationMetadata", paginationMetadata);
        return "manageOrders";
    }

    @Secured({ "ROLE_CUSTOMER" })
    @GetMapping("/orders/personal")
    public String getPersonalOrdersPage(Model model) {
        SuccessResponse<List<OrderDTO>> successResponse = this.orderService.getPersonalOrders(PAGEABLE, Optional.empty(), Optional.empty());
        List<OrderDTO> orderDtos = successResponse.getData();
        PaginationMetadataDTO paginationMetadata = successResponse.getPaginationMetadata();
        model.addAttribute("orders", orderDtos);
        model.addAttribute("paginationMetadata", paginationMetadata);
        return "personalOrders";
    }

    @Secured({ "ROLE_CUSTOMER" })
    @GetMapping("/orders/{orderId}/detail")
    public String getOrderDetailPage(Model model, @PathVariable long orderId) {
        SuccessResponse<OrderDTO> successResponse = this.orderService.getOrderById(orderId);
        OrderDTO orderDto = successResponse.getData();
        model.addAttribute("order", orderDto);
        return "orderDetail";
    }
}
