package com.shoppingcart.order;

import com.shoppingcart.order.Order.OrderItem;
import java.util.List;

record OrderDto(
    String orderId,
    double totalPrice,
    double totalVat,
    double totalWithVat,
    List<OrderItemDTO> product
) {

  static OrderDto from(Order order) {
    List<OrderItemDTO> products = order
        .products()
        .stream()
        .map(OrderItemDTO::from)
        .toList();
    return new OrderDto(order.orderId(), order.totalPrice(), order.totalVat(), order.totalWithVat(),
        products);
  }

  record OrderItemDTO(ProductDto product, int quantity) {

    static OrderItemDTO from(OrderItem orderItem) {
      return new OrderItemDTO(ProductDto.from(orderItem.product()), orderItem.quantity());
    }
  }

}
