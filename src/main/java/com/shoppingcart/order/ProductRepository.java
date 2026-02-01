package com.shoppingcart.order;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

interface ProductRepository extends MongoRepository<Product, String> {

  Optional<Product> findByName(String name);

  void deleteByName(String name);

  @Query("{ name: ?0 }")
  @Update("{ set: { price: ?1, description: ?2, vatRate: ?3, priceWithVat: ?4 } }")
  void updateProductByName(String name, String description, double price, double vatRate, double priceWithVat);
}
