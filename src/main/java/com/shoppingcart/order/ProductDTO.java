package com.shoppingcart.order;

record ProductDTO(
    String productName,
    String description,
    double price,
    double vatRate,
    double priceWithVat
) {

  static ProductDTO from(Product product) {
    double totalPrice = (product.price() * product.vatRate()) + product.price();
    return new ProductDTO(product.name(), product.description(), product.price(), product.vatRate(),
        totalPrice);
  }

}
