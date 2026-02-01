package com.shoppingcart.order;

import java.math.BigDecimal;
import java.math.RoundingMode;

record ProductDto(
    String name,
    String description,
    double price,
    double vatRate,
    double priceWithVat
) {

  static ProductDto from(Product product) {
    double totalPrice = (product.price() * product.vatRate()) + product.price();
    totalPrice = BigDecimal.valueOf(totalPrice)
        .setScale(2, RoundingMode.HALF_UP)
        .doubleValue();
    return new ProductDto(product.name(), product.description(), product.price(), product.vatRate(),
        totalPrice);
  }

}
