package com.shoppingcart.order;

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
  List<ProductDTO> getProducts() {
    return productService.getProducts();
  }

  @GetMapping("products/{name}")
  ProductDTO getProduct(@PathVariable String name) {
    return productService.getProductByName(name);
  }

  @PostMapping("/products")
  ProductDTO addProduct(@RequestBody AddProductRequest request) {
    return productService.addProduct(request);
  }

  @PutMapping("/products/{name}")
  ProductDTO updateProduct(
      @PathVariable
      String name,
      @RequestBody
      ProductUpdateRequest request) {
    return productService.updateProduct(name, request);
  }

  @DeleteMapping("/products/{name}")
  void deleteProduct(@PathVariable String name) {
    productService.deleteProduct(name);
  }

  record ProductUpdateRequest(double price, double vatRate) {

  }

  record AddProductRequest(String name, String description, double price, double vatRate) {

  }
}
