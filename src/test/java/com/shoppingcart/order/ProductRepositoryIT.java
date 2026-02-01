package com.shoppingcart.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.shoppingcart.MongoDBContainers;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@DataMongoTest
@EnableMongoRepositories(basePackageClasses = ProductRepository.class)
@ImportTestcontainers(MongoDBContainers.class)
class ProductRepositoryIT {

  @Autowired
  ProductRepository repository;

  Product mouse;
  Product keyboard;

  @BeforeEach
  void setup() {
    mouse = new Product("mouseId", "mouse-m2",
        "mouse Bluetooth compatible with different operating system", 120.50D, 0.22D, 147.01D);
    keyboard = new Product("keyboard", "keyboard-d1",
        "keyboard Bluetooth compatible with different operating system", 150.20D, 0.22D, 183.24D);
    repository.saveAll(List.of(mouse, keyboard));
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void findByNameShouldReturnTheSearched() {
    Product found = repository.findByName("mouse-m2")
        .orElseThrow();

    assertThat(found)
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(mouse);
  }

  @Test
  void findByNameShouldReturnEmptyWhenProductNotExists() {
    Optional<Product> notExisting = repository.findByName("notExisting");

    assertThat(notExisting)
        .isEmpty();
  }

  @Test
  void deleteProductByNameShouldDeleteProduct() {
    repository.deleteByName("keyboard-d1");

    assertThat(repository.findAll())
        .doesNotContain(keyboard);
  }

  @Test
  void deleteProductByNameShouldNoDeleteWhenProductNotExists() {
    repository.deleteByName("notExisting");

    assertThat(repository.findAll())
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
        .containsExactlyInAnyOrder(mouse, keyboard);
  }

  @Test
  void updateProductByNameShouldUpdatePriceAndVatBy() {
    repository.updateProductByName("mouse-m2", "mouse Bluetooth", 140.0D, 0.21D,
        169.40D);

    assertThat(repository.findByName("mouse-m2"))
        .contains(new Product("mouseId", "mouse-m2", "mouse Bluetooth", 140.0D, 0.21D, 169.40D));
  }

}