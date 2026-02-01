package com.shoppingcart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

@ImportTestcontainers(MongoDBContainers.class)
@SpringBootTest
class ShoppingCartApplicationTests {

  @Test
  void contextLoads() {
  }

}
