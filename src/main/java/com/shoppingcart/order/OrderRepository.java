package com.shoppingcart.order;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

interface OrderRepository extends MongoRepository<Order, String> {

  Optional<Order> findByOrderId(String orderId);

  void deleteOrderByOrderId(String orderId);
}
