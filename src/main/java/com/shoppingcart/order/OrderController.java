package com.shoppingcart.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
class OrderController {

  private final OrderService orderService;

  @GetMapping("/orders")
  List<OrderDTO> getOrders() {
    return orderService.getOrders();
  }

  @GetMapping("/orders/{id}")
  OrderDTO getOrder(@PathVariable String id) {
    return orderService.getOrderByOrderId(id);
  }

  @PostMapping("/orders")
  OrderDTO addOrder(
      @RequestBody
      @Valid
      AddRequest request) {
    return orderService.addOrder(request);
  }

  @PutMapping("/orders/{id}")
  void updateOrder(
      @PathVariable
      String id,
      @RequestBody
      @Valid
      UpdateOrderRequest request
  ) {
    orderService.updateOrder(id, request);
  }

  @DeleteMapping("/orders/{id}")
  void deleteOrder(@PathVariable String id) {
    orderService.deleteOrder(id);
  }

  record AddRequest(@NotEmpty List<RequestItem> products) {

  }

  record UpdateOrderRequest(@NotEmpty List<RequestItem> products) {

  }

  record RequestItem(@NotNull String productName, @Positive int quantity) {

  }
}
