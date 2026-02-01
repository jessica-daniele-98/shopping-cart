package com.shoppingcart.order;

class ItemOrderNotFoundException extends RuntimeException {

  ItemOrderNotFoundException(String message) {
    super(message);
  }
}
