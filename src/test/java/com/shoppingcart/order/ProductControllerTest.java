package com.shoppingcart.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.shoppingcart.order.ProductController.AddProductRequest;
import com.shoppingcart.order.ProductController.ProductUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(ProductController.class)
@ContextConfiguration(
    classes = {
        ProductController.class,
        ProductService.class,
        ProductControllerAdvice.class
    }
)
@AutoConfigureMockMvc
class ProductControllerTest {

  @Autowired
  MockMvcTester mockMvcTester;

  @MockitoBean
  ProductService service;

  ProductDTO pencil;
  ProductDTO pen;

  @BeforeEach
  void setup() {
    pencil = new ProductDTO("pencil", "pencil HB", 2.3D, 0.22D, 2.81D);
    pen = new ProductDTO("pen", "blue pen", 2.3D, 0.22D, 2.81D);
  }

  @Test
  void getProductsShouldReturnAllProducts() {
    given(service.getProducts())
        .willReturn(List.of(pencil, pen));

    MvcTestResult result = mockMvcTester
        .get()
        .uri("/products")
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .isEqualTo("""
            [
              {
                "name": "pencil",
                "description": "pencil HB",
                "price": 2.3,
                "vatRate": 0.22,
                "priceWithVat": 2.81
              },
              {
                "name": "pen",
                "description": "blue pen",
                "price": 2.3,
                "vatRate": 0.22,
                "priceWithVat": 2.81
              }
            ]
            """);
  }

  @Test
  void getProductByNameShouldReturnTheRequestProduct() {
    given(service.getProductByName("pencil"))
        .willReturn(pencil);

    MvcTestResult result = mockMvcTester
        .get()
        .uri("/products/{id}", "pencil")
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .isEqualTo("""
            {
              "name": "pencil",
              "description": "pencil HB",
              "price": 2.3,
              "vatRate": 0.22,
              "priceWithVat": 2.81
            }
            """);
  }

  @Test
  void getProductByNameShouldReturnNotFoundWhenProductNotExists() {
    given(service.getProductByName("product"))
        .willThrow(ProductNotFoundException.class);

    MvcTestResult result = mockMvcTester
        .get()
        .uri("/products/{id}", "product")
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND);
  }

  @Test
  void addProductShouldReturnTheAddedProduct() {
    given(service.addProduct(new AddProductRequest("pencil", "pencil HB", 2.3D, 0.22D)))
        .willReturn(pencil);

    MvcTestResult result = mockMvcTester
        .post()
        .uri("/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "pencil",
              "description": "pencil HB",
              "price": 2.3,
              "vatRate": 0.22
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .isEqualTo("""
            {
              "name": "pencil",
              "description": "pencil HB",
              "price": 2.3,
              "vatRate": 0.22,
              "priceWithVat": 2.81
            }
            """);
  }

  @Test
  void addProductShouldReturnBadRequestWhenTheProductAlreadyExists() {
    given(service.addProduct(new AddProductRequest("pencil", "pencil HB", 2.3D, 0.22D)))
        .willThrow(DuplicateKeyException.class);

    MvcTestResult result = mockMvcTester
        .post()
        .uri("/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "pencil",
              "description": "pencil HB",
              "price": 2.3,
              "vatRate": 0.22
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST);
  }

  @Test
  void addProductShouldReturnBadRequestWhenVatRateIsNegativeInTheRequest() {
    given(service.addProduct(new AddProductRequest("pencil", "pencil HB", 2.3D, -2.4D)))
        .willThrow(IllegalArgumentException.class);

    MvcTestResult result = mockMvcTester
        .post()
        .uri("/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "name": "pencil",
              "description": "pencil HB",
              "price": 2.3,
              "vatRate": -2.4
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST);
  }

  @Test
  void updateProductShouldUpdateProduct() {
    MvcTestResult result = mockMvcTester
        .put()
        .uri("/products/{name}", "pen")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "price": 1.2,
              "vatRate": 0.21,
              "description": "description"
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatusOk();
    verify(service).updateProduct("pen", new ProductUpdateRequest(1.2D, 0.21D, "description"));
  }

  @Test
  void updateProductShouldReturnNotFoundWhenProductNotExists() {
    doThrow(ProductNotFoundException.class)
        .when(service)
        .updateProduct("product", new ProductUpdateRequest(2.3D, 0.22D, "description"));

    MvcTestResult result = mockMvcTester
        .put()
        .uri("/products/{name}", "product")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "price": 2.3,
              "vatRate": 0.22,
              "description": "description"
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND);
  }

  @Test
  void updateProductShouldReturnBadRequestWhenDescriptionIsMissingInTheRequest() {
    doThrow(IllegalArgumentException.class)
        .when(service)
        .updateProduct("product", new ProductUpdateRequest(2.3D, 0.22D, null));

    MvcTestResult result = mockMvcTester
        .put()
        .uri("/products/{name}", "product")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "price": 2.3,
              "vatRate": 0.22
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST);
  }

  @Test
  void deleteProductShouldDeleteProduct() {
    MvcTestResult result = mockMvcTester
        .delete()
        .uri("/products/{name}", "pen")
        .exchange();

    assertThat(result)
        .hasStatusOk();
    verify(service).deleteProduct("pen");
  }


}