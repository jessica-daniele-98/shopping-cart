package com.shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

@ImportTestcontainers(MongoDBContainers.class)
class TestShoppingCartApplication {

  static void main(String[] args) {
    SpringApplication
        .from(ShoppingCartApplication::main)
        .run(args);
  }

}
