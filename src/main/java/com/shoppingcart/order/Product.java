package com.shoppingcart.order;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
record Product(
    @Indexed(unique = true)
    String name,
    String description,
    double price,
    double vatRate,
    double priceWithVat
) {

  double calculateVat() {
    return this.price * this.vatRate;
  }

}
