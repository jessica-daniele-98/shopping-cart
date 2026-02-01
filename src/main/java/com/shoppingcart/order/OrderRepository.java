package com.shoppingcart.order;

import com.shoppingcart.order.Order.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

interface OrderRepository extends MongoRepository<Order, String> {

  Optional<Order> findByOrderId(String orderId);

  void deleteOrderByOrderId(String orderId);

  @Query("{ orderId:  ?0 }")
  @Update("{ set: { totalPrice: ?1, totalVat:  ?2, totalWithVat:  ?3, products: ?4 } }")
  void updateOrder(String id, double totalPrice, double totalVat, double totalPriceWithVat, List<OrderItem> products);

}
