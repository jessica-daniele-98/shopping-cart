package com.shoppingcart.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.shoppingcart.order.OrderController.AddRequest;
import com.shoppingcart.order.OrderController.RequestItem;
import com.shoppingcart.order.OrderController.UpdateOrderRequest;
import com.shoppingcart.order.OrderDTO.OrderItemDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(OrderController.class)
@ContextConfiguration(
    classes = {
        OrderController.class,
        OrderService.class,
        OrderControllerAdvice.class
    }
)
@AutoConfigureMockMvc
class OrderControllerTest {

  @Autowired
  MockMvcTester mockMvcTester;

  @MockitoBean
  OrderService service;

  OrderDTO order1;
  OrderDTO order2;

  @BeforeEach
  void setup() {
    ProductDTO pencil = new ProductDTO("pencil", "pencil HB", 2.3, 0.22, 2.81);
    ProductDTO pen = new ProductDTO("pen", "blue pen", 2.3, 0.22, 2.81);
    order1 = new OrderDTO(
        "1",
        4.6,
        0.5,
        5.1,
        List.of(new OrderItemDTO(pencil, 2))
    );
    order2 = new OrderDTO(
        "2",
        6.9,
        0.7,
        7.6,
        List.of(new OrderItemDTO(pen, 3))
    );
  }

  @Test
  void getOrdersShouldReturnAllOrders() {
    given(service.getOrders())
        .willReturn(List.of(order1, order2));

    MvcTestResult result = mockMvcTester
        .get()
        .uri("/orders")
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .isEqualTo("""
            [
              {
                "orderId": "1",
                "totalPrice": 4.6,
                "totalVat": 0.5,
                "totalWithVat": 5.1,
                "product": [
                  {
                    "product": {
                      "name": "pencil",
                      "description": "pencil HB",
                      "price": 2.3,
                      "vatRate": 0.22,
                      "priceWithVat": 2.81
                    },
                    "quantity": 2
                  }
                ]
              },
              {
                "orderId": "2",
                "totalPrice": 6.9,
                "totalVat": 0.7,
                "totalWithVat": 7.6,
                "product": [
                  {
                    "product": {
                      "name": "pen",
                      "description": "blue pen",
                      "price": 2.3,
                      "vatRate": 0.22,
                      "priceWithVat": 2.81
                    },
                    "quantity": 3
                  }
                ]
              }
            ]
            """);
  }

  @Test
  void getOrderShouldReturnRequestedOrder() {
    given(service.getOrderByOrderId("1"))
        .willReturn(order1);

    MvcTestResult result = mockMvcTester
        .get()
        .uri("/orders/{id}", "1")
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .isEqualTo("""
            {
              "orderId": "1",
              "totalPrice": 4.6,
              "totalVat": 0.5,
              "totalWithVat": 5.1,
              "product": [
                {
                  "product": {
                    "name": "pencil",
                    "description": "pencil HB",
                    "price": 2.3,
                    "vatRate": 0.22,
                    "priceWithVat": 2.81
                  },
                  "quantity": 2
                }
              ]
            }
            """);
  }

  @Test
  void getOrderShouldReturnNotFoundWhenOrderDoesNotExist() {
    given(service.getOrderByOrderId("10"))
        .willThrow(OrderNotFoundException.class);

    MvcTestResult result = mockMvcTester
        .get()
        .uri("/orders/{id}", "10")
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND);
  }

  @Test
  void addOrderShouldReturnCreatedOrder() {
    AddRequest req = new AddRequest(
        List.of(new RequestItem("pencil", 2))
    );

    given(service.addOrder(req))
        .willReturn(order1);

    MvcTestResult result = mockMvcTester
        .post()
        .uri("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "products": [
                {"productName": "pencil", "quantity": 2}
              ]
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .isEqualTo("""
            {
              "orderId": "1",
              "totalPrice": 4.6,
              "totalVat": 0.5,
              "totalWithVat": 5.1,
              "product": [
                {
                  "product": {
                    "name": "pencil",
                    "description": "pencil HB",
                    "price": 2.3,
                    "vatRate": 0.22,
                    "priceWithVat": 2.81
                  },
                  "quantity": 2
                }
              ]
            }
            """);
  }

  @Test
  void addOrderShouldReturnBadRequestWhenProductsIsEmpty() {
    MvcTestResult result = mockMvcTester
        .post()
        .uri("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "products": []
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST);
  }

  @Test
  void updateOrderShouldUpdateOrder() {
    UpdateOrderRequest req =
        new UpdateOrderRequest(List.of(new RequestItem("pen", 3)));

    MvcTestResult result = mockMvcTester
        .put()
        .uri("/orders/{id}", "1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "products": [
                {"productName": "pen", "quantity": 3}
              ]
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatusOk();

    verify(service).updateOrder("1", req);
  }

  @Test
  void updateOrderShouldReturnNotFoundWhenOrderDoesNotExist() {
    UpdateOrderRequest req =
        new UpdateOrderRequest(List.of(new RequestItem("pen", 3)));

    doThrow(OrderNotFoundException.class)
        .when(service)
        .updateOrder("10", req);

    MvcTestResult result = mockMvcTester
        .put()
        .uri("/orders/{id}", "10")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "products": [
                {"productName": "pen", "quantity": 3}
              ]
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND);
  }

  @Test
  void updateOrderShouldReturnBadRequestWhenProductsIsEmpty() {
    MvcTestResult result = mockMvcTester
        .put()
        .uri("/orders/{id}", "1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "products": []
            }
            """)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST);
  }

  @Test
  void deleteOrderShouldDeleteOrder() {
    MvcTestResult result = mockMvcTester
        .delete()
        .uri("/orders/{id}", "1")
        .exchange();

    assertThat(result)
        .hasStatusOk();

    verify(service).deleteOrder("1");
  }

  @Test
  void deleteOrderShouldReturnNotFoundWhenOrderDoesNotExist() {
    doThrow(OrderNotFoundException.class)
        .when(service)
        .deleteOrder("10");

    MvcTestResult result = mockMvcTester
        .delete()
        .uri("/orders/{id}", "10")
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND);
  }
}
