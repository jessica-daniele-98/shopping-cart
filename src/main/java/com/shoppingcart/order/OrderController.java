package com.shoppingcart.order;

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
  OrderDTO addOrder(@RequestBody AddRequest request) {
    return orderService.addOrder(request);
  }

  @PutMapping("/orders/{id}")
  OrderDTO updateOrder(
      @PathVariable
      String id,
      @RequestBody
      UpdateOrderRequest request
  ) {
    return orderService.updateOrder(id, request);
  }

  @DeleteMapping("/orders/{id}")
  void deleteOrder(@PathVariable String id) {
    orderService.deleteOrder(id);
  }

  record AddRequest(List<RequestItem> products) {

  }

  record UpdateOrderRequest(List<RequestItem> products) {

  }

  record RequestItem(String productName, int quantity) {

  }
}
