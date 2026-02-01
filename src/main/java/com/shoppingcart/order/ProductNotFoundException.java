package com.shoppingcart.order;

class ProductNotFoundException extends RuntimeException {

  ProductNotFoundException(String message) {
    super(message);
  }
}
