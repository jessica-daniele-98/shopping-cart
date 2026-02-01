package com.shoppingcart.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.shoppingcart.MongoDBContainers;
import com.shoppingcart.order.Order.OrderItem;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@DataMongoTest
@EnableMongoRepositories(basePackageClasses = OrderRepository.class)
@ImportTestcontainers(MongoDBContainers.class)
class OrderRepositoryIT {

  @Autowired
  OrderRepository orderRepository;

  Order order;

  @BeforeEach
  void setup() {
    Product product = new Product("keyboard", "keyboard-d1",
        "keyboard Bluetooth compatible with different operating system", 150.20D, 0.22D, 183.24D);
    OrderItem orderItem = new OrderItem(product, 1);
    order = new Order("id", "orderId", LocalDate.now(), 150.20D, 33.04D, 183.24D,
        List.of(orderItem));
    orderRepository.save(order);
  }

  @AfterEach
  void tearDown() {
    orderRepository.deleteAll();
  }

  @Test
  void findByOrderIdShouldReturnTheSearchedOrder() {
    Optional<Order> foundOrder = orderRepository.findByOrderId("orderId");

    assertThat(foundOrder)
        .contains(order);
  }

  @Test
  void findByOrderIdShouldReturnEmptyWhenOrderNotExists() {
    Optional<Order> foundOrder = orderRepository.findByOrderId("notExisting");

    assertThat(foundOrder)
        .isEmpty();
  }

  @Test
  void deleteOrderByOrderIdShouldDeleteOrder() {
    orderRepository.deleteOrderByOrderId("orderId");

    assertThat(orderRepository.findAll())
        .isEmpty();
  }

  @Test
  void updateOrderShouldUpdateTheOrder() {
    Product product = new Product("keyboard", "keyboard-d1",
        "keyboard Bluetooth compatible with different operating system", 150.20D, 0.22D, 183.24D);
    OrderItem orderItem = new OrderItem(product, 2);

    orderRepository.updateOrder("orderId", 300.40D, 66.08D, 366.48D, List.of(orderItem));

    assertThat(orderRepository.findAll())
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt")
        .containsExactlyInAnyOrder(
            new Order("id", "orderId", LocalDate.now(), 300.4D, 66.08D, 366.48D,
                List.of(orderItem)));
  }

}