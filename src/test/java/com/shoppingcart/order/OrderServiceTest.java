package com.shoppingcart.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shoppingcart.order.Order.OrderItem;
import com.shoppingcart.order.OrderController.AddRequest;
import com.shoppingcart.order.OrderController.RequestItem;
import com.shoppingcart.order.OrderController.UpdateOrderRequest;
import com.shoppingcart.order.OrderDto.OrderItemDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  OrderRepository orderRepository;

  @Mock
  ProductService productService;

  OrderService orderService;

  Product pen;
  Product pencil;

  @BeforeEach
  void setup() {
    orderService = new OrderService(orderRepository, productService);
    pen = new Product("pen", "blue pen", 10.0D, 0.20D, 12.0D);
    pencil = new Product("pencil", "pencil HB", 5.0D, 0.10D, 5.5D);
  }

  @Test
  void getOrdersShouldReturnFoundOrder() {
    OrderItem item = new OrderItem(pen, 2);
    Order order = new Order("order-1", LocalDate.now(), 20.0D, 4.0D, 24.0D, List.of(item));
    given(orderRepository.findAll()).willReturn(List.of(order));

    List<OrderDto> result = orderService.getOrders();

    assertThat(result)
        .hasSize(1)
        .containsExactlyInAnyOrder(OrderDto.from(order));
  }

  @Test
  void getOrderByOrderIdShouldReturnFoundOrder() {
    OrderItem item = new OrderItem(pencil, 3);
    Order order = new Order("order-2", LocalDate.now(), 15.0D, 1.5D, 16.5D, List.of(item));
    given(orderRepository.findByOrderId("order-2")).willReturn(Optional.of(order));

    OrderDto result = orderService.getOrderByOrderId("order-2");

    assertThat(result)
        .isEqualTo(OrderDto.from(order));
  }

  @Test
  void getOrderByOrderIdShouldThrownOrderNotFoundExceptionWhenOrderNotExists() {
    given(orderRepository.findByOrderId("missing")).willReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class, () -> orderService.getOrderByOrderId("missing"));
  }

  @Test
  void addOrderShouldAddTheRequestOrder() {
    RequestItem reqItemA = new RequestItem("prodA", 2);
    RequestItem reqItemB = new RequestItem("prodB", 1);
    AddRequest addRequest = new AddRequest(List.of(reqItemA, reqItemB));
    given(productService.findProductByName("prodA")).willReturn(Optional.of(pen));
    given(productService.findProductByName("prodB")).willReturn(Optional.of(pencil));
    ArgumentCaptor<Order> savedCaptor = ArgumentCaptor.forClass(Order.class);
    when(orderRepository.save(savedCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

    OrderDto result = orderService.addOrder(addRequest);

    assertThat(result)
        .usingRecursiveComparison()
        .ignoringFields("orderId")
        .isEqualTo(new OrderDto("order-id", 25.0D, 4.5D, 29.5D, List.of(
            new OrderItemDTO(ProductDto.from(pen), 2),
            new OrderItemDTO(ProductDto.from(pencil), 1)
        )));
    Order saved = savedCaptor.getValue();
    assertThat(saved)
        .usingRecursiveComparison()
        .ignoringFields("id", "orderId", "createdAt")
        .isEqualTo(new Order("order-id", LocalDate.now(), 25.0D, 4.5D, 29.5D, List.of(
            new OrderItem(pen, 2),
            new OrderItem(pencil, 1)
        )));
  }

  @Test
  void addOrderShouldThrowItemOrderNotFoundWhenProductNotExists() {
    RequestItem reqItem = new RequestItem("unknownProd", 1);
    AddRequest addRequest = new AddRequest(List.of(reqItem));

    given(productService.findProductByName("unknownProd")).willReturn(Optional.empty());

    assertThrows(ItemOrderNotFoundException.class, () -> orderService.addOrder(addRequest));
  }

  @Test
  void updateOrderShouldUpdateTheOrderWithCorrectTotals() {
    RequestItem reqItem = new RequestItem("pen", 3);
    UpdateOrderRequest updateRequest = new UpdateOrderRequest(List.of(reqItem));

    given(productService.findProductByName("pen")).willReturn(Optional.of(pen));

    orderService.updateOrder("some-id", updateRequest);

    verify(orderRepository).updateOrder(
        "some-id",
        30.0D,
        6.0D,
        36.0D,
        List.of(new OrderItem(pen, 3))
    );
  }

  @Test
  void deleteOrderShouldDeleteOrder() {
    orderService.deleteOrder("order-to-delete");
    verify(orderRepository).deleteOrderByOrderId("order-to-delete");
  }

}