package com.shoppingcart.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
class ProductController {

  private final ProductService productService;

  @GetMapping("/products")
  List<ProductDto> getProducts() {
    return productService.getProducts();
  }

  @GetMapping("products/{name}")
  ProductDto getProductByName(@PathVariable String name) {
    return productService.getProductByName(name);
  }

  @PostMapping("/products")
  ProductDto addProduct(
      @RequestBody
      @Valid
      AddProductRequest request) {
    return productService.addProduct(request);
  }

  @PutMapping("/products/{name}")
  void updateProduct(
      @PathVariable
      String name,
      @RequestBody
      @Valid
      ProductUpdateRequest request) {
    productService.updateProduct(name, request);
  }

  @DeleteMapping("/products/{name}")
  void deleteProduct(@PathVariable String name) {
    productService.deleteProduct(name);
  }

  record ProductUpdateRequest(
      @Positive
      double price,
      @Positive
      double vatRate,
      @NotNull
      String description) {

  }

  record AddProductRequest(
      @NotNull
      String name,
      @NotNull
      String description,
      @Positive
      double price,
      @Positive
      double vatRate) {

  }
}
