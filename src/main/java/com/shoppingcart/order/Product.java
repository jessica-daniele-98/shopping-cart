package com.shoppingcart.order;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
record Product(
    @Id
    String id,
    @Indexed(unique = true)
    String name,
    String description,
    double price,
    double vatRate,
    double priceWithVat
) {

  static Product from(ProductDTO productDto) {
    return new Product(null, productDto.productName(), productDto.description(), productDto.price(),
        productDto.vatRate(), productDto.priceWithVat());
  }

  double calculateVat() {
    return this.price * this.vatRate;
  }

}
