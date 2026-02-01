package com.shoppingcart.order;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

interface ProductRepository extends MongoRepository<Product, String> {

  Optional<Product> findByName(String name);

  void deleteByName(String name);

  @Query("{ name: ?0 }")
  @Update("{ set: { price: ?1, vatRate: ?2, priceWithVat: ?3 } }")
  Product updateProductByName(String name, double price, double vatRate, double priceWithVat);
}
