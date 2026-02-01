package com.shoppingcart.order;

import com.shoppingcart.order.ProductController.AddProductRequest;
import com.shoppingcart.order.ProductController.ProductUpdateRequest;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class ProductService {

  private final ProductRepository productRepository;

  List<ProductDTO> getProducts() {
    return productRepository
        .findAll()
        .stream()
        .map(ProductDTO::from)
        .toList();
  }

  ProductDTO getProductByName(String name) throws ProductNotFoundException {
    return this.findProductByName(name)
        .map(ProductDTO::from)
        .orElseThrow(
            () -> new ProductNotFoundException("Product %s not found".formatted(name)));
  }

  Optional<Product> findProductByName(String name) {
    return productRepository.findByName(name);
  }

  ProductDTO addProduct(AddProductRequest request) {
    double priceWithVat = calculatePriceWithVat(request.price(), request.vatRate());
    Product product = new Product(
        null,
        request.name(),
        request.description(),
        request.price(),
        request.vatRate(),
        priceWithVat);
    return ProductDTO.from(productRepository.save(product));
  }

  void deleteProduct(String name) {
    productRepository.deleteByName(name);
  }

  ProductDTO updateProduct(String productName, ProductUpdateRequest updateRequest) {
    double newPrice = updateRequest.price();
    double newVatRate = updateRequest.vatRate();
    double newPriceWithVat = calculatePriceWithVat(newPrice, newVatRate);
    return ProductDTO.from(productRepository
        .updateProductByName(productName, newPrice, newVatRate, newPriceWithVat));
  }

  private double calculatePriceWithVat(double price, double vatRate) {
    return price + (price * vatRate);
  }

}
