package com.shoppingcart.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.shoppingcart.order.ProductController.AddProductRequest;
import com.shoppingcart.order.ProductController.ProductUpdateRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  ProductRepository productRepository;

  ProductService productService;
  Product mouseProduct;
  Product keyboardProduct;

  @BeforeEach
  void setup() {
    productService = new ProductService(productRepository);
    mouseProduct = new Product(null, "mouse-m2",
        "mouse Bluetooth compatible with different operating system", 120.50D, 0.22D, 147.01D);
    keyboardProduct = new Product(null, "keyboard-d1",
        "keyboard Bluetooth compatible with different operating system", 150.20D, 0.22D, 183.24D);
  }

  @Test
  void getProductsShouldReturnAllProducts() {
    given(productRepository.findAll())
        .willReturn(List.of(mouseProduct, keyboardProduct));

    List<ProductDTO> products = productService.getProducts();

    assertThat(products)
        .containsExactlyInAnyOrder(ProductDTO.from(mouseProduct), ProductDTO.from(keyboardProduct));
  }

  @Test
  void getProductShouldReturnEmptyListWhenNoProductsArePresent() {
    given(productRepository.findAll())
        .willReturn(List.of());

    List<ProductDTO> products = productService.getProducts();

    assertThat(products)
        .isEmpty();
  }

  @Test
  void getProductByNameShouldReturnTheSearchedProduct() {
    given(productRepository.findByName("mouse-m2"))
        .willReturn(Optional.of(mouseProduct));

    ProductDTO foundProduct = productService.getProductByName("mouse-m2");

    assertThat(foundProduct)
        .isEqualTo(ProductDTO.from(mouseProduct));
  }

  @Test
  void getProductByNameShouldThrownExceptionWhenProductNotExists() {
    given(productRepository.findByName("product"))
        .willReturn(Optional.empty());

    assertThatExceptionOfType(ProductNotFoundException.class)
        .isThrownBy(() -> productService.getProductByName("product"));
  }

  @Test
  void findProductByNameShouldReturnSearchedProduct() {
    given(productRepository.findByName("mouse-m2"))
        .willReturn(Optional.of(mouseProduct));

    Optional<Product> product = productService.findProductByName("mouse-m2");

    assertThat(product)
        .contains(mouseProduct);
  }

  @Test
  void findProductByNameShouldReturnEmptyWhenProductNotExists() {
    given(productRepository.findByName("product"))
        .willReturn(Optional.empty());

    Optional<Product> product = productService.findProductByName("product");

    assertThat(product)
        .isEmpty();
  }

  @Test
  void addProductShouldAddProduct() {
    Product pencil = new Product(null, "pencil", "pencil HB", 2.30D, 0.22D, 2.81D);
    given(productRepository.save(pencil))
        .willReturn(pencil);

    ProductDTO saved = productService.addProduct(
        new AddProductRequest("pencil", "pencil HB", 2.30D, 0.22D));

    assertThat(saved)
        .extracting(ProductDTO::name)
        .isEqualTo(pencil.name());
    assertThat(saved)
        .extracting(ProductDTO::price)
        .isEqualTo(pencil.price());
    assertThat(saved)
        .extracting(ProductDTO::vatRate)
        .isEqualTo(pencil.vatRate());
  }

  @Test
  void addProductShouldReturnDuplicateKeyExceptionIfProductAlreadyExists() {
    given(productRepository.save(mouseProduct))
        .willThrow(DuplicateKeyException.class);

    assertThatThrownBy(() -> productService.addProduct(
        new AddProductRequest("mouse-m2",
            "mouse Bluetooth compatible with different operating system", 120.50D, 0.22D)))
        .isInstanceOf(DuplicateKeyException.class);

  }

  @Test
  void deleteProductShouldDeleteTheProduct() {
    doNothing()
        .when(productRepository)
        .deleteByName("mouse-m2");

    productService.deleteProduct("mouse-m2");

    verify(productRepository)
        .deleteByName("mouse-m2");
  }

  @Test
  void updateProductShouldUpdateTheProduct() {
    doNothing()
        .when(productRepository)
        .updateProductByName("mouse-m2", "mouse Bluetooth", 140.0D, 0.21D, 169.40D);

    productService.updateProduct(
        "mouse-m2",
        new ProductUpdateRequest(140.0D, 0.21D, "mouse Bluetooth"));

    verify(productRepository)
        .updateProductByName("mouse-m2", "mouse Bluetooth", 140.0D, 0.21D, 169.40D);
  }

}