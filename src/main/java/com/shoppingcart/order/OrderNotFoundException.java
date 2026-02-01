package com.shoppingcart.order;

class OrderNotFoundException extends RuntimeException {

  OrderNotFoundException(String message) {
    super(message);
  }
}
