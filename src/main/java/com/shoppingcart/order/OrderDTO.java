package com.shoppingcart.order;

import com.shoppingcart.order.Order.OrderItem;
import java.util.List;

record OrderDTO(
    String orderId,
    double totalPrice,
    double totalVat,
    double totalWithVat,
    List<OrderItemDTO> product
) {

  static OrderDTO from(Order order) {
    List<OrderItemDTO> products = order
        .products()
        .stream()
        .map(OrderItemDTO::from)
        .toList();
    return new OrderDTO(order.orderId(), order.totalPrice(), order.totalVat(), order.totalWithVat(),
        products);
  }

  record OrderItemDTO(ProductDTO product, int quantity) {

    static OrderItemDTO from(OrderItem orderItem) {
      return new OrderItemDTO(ProductDTO.from(orderItem.product()), orderItem.quantity());
    }
  }

}
