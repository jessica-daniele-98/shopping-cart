package com.shoppingcart.order;

import com.shoppingcart.order.Order.OrderItem;
import com.shoppingcart.order.OrderController.AddRequest;
import com.shoppingcart.order.OrderController.AddRequest.RequestItem;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class OrderService {

  private final OrderRepository orderRepository;
  private final ProductService productService;

  List<OrderDTO> getOrders() {
    return orderRepository
        .findAll()
        .stream()
        .map(OrderDTO::from)
        .toList();
  }

  OrderDTO getOrderByOrderId(String orderId) {
    return orderRepository
        .findByOrderId(orderId)
        .map(OrderDTO::from)
        .orElseThrow(() -> new OrderNotFoundException("Order %s not found".formatted(orderId)));
  }

  OrderDTO addOrder(AddRequest request) {
    List<OrderItem> orderItems = request
        .products()
        .stream()
        .map(this::toOrderItem)
        .toList();
    double totalPrice = calculateTotalPrice(orderItems);
    double totalVat = calculateTotalVat(orderItems);
    double priceWithVat = calculateTotalPriceWithVat(orderItems);
    Order order = new Order(
        null,
        UUID.randomUUID().toString(),
        LocalDate.now(),
        totalPrice,
        totalVat,
        priceWithVat,
        orderItems);
    return OrderDTO.from(orderRepository.save(order));
  }

  void deleteOrder(String orderId) {
    orderRepository.deleteOrderByOrderId(orderId);
  }

  private double calculateTotalPriceWithVat(List<OrderItem> items) {
    List<Double> prices = items
        .stream()
        .map(item -> item.product().priceWithVat() * item.quantity())
        .toList();
    return prices
        .stream()
        .mapToDouble(Double::doubleValue)
        .sum();
  }

  private double calculateTotalPrice(List<OrderItem> items) {
    List<Double> prices = items
        .stream()
        .map(item -> item.product().price() * item.quantity())
        .toList();
    return prices
        .stream()
        .mapToDouble(Double::doubleValue)
        .sum();
  }

  private double calculateTotalVat(List<OrderItem> items) {
    List<Double> vats = items
        .stream()
        .map(item -> item.product().calculateVat() * item.quantity())
        .toList();
    return vats
        .stream()
        .mapToDouble(Double::doubleValue)
        .sum();
  }

  private OrderItem toOrderItem(RequestItem item) {
    Product product = productService.findProductByName(item.productName())
        .orElseThrow(() -> new ItemOrderNotFoundException(
            "Product %s not found, please verify if the product name is correct"
                .formatted(item.productName())));
    return new OrderItem(product, item.quantity());
  }

}
