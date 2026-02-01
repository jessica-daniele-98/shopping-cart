package com.shoppingcart.order;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
record Order(
    @Indexed(unique = true)
    String orderId,
    LocalDate createdAt,
    double totalPrice,
    double totalVat,
    double totalWithVat,
    List<OrderItem> products
) {

  record OrderItem(Product product, int quantity) {

  }

}