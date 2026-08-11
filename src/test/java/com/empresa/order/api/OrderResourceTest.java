package com.empresa.order.api;

import com.empresa.order.domain.Order;
import com.empresa.order.domain.OrderStatus;
import com.empresa.order.domain.OrderEvent;
import com.empresa.order.domain.PaymentEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderResourceTest {

    @Test
    public void testOrderDomainModel() {
        Order order = new Order();
        order.setId(10L);
        order.setCustomerName("Bruno Dias");
        order.setProductName("Teclado Gamer");
        order.setQuantity(3);
        order.setPrice(150.0);
        order.setStatus(OrderStatus.PENDING);

        Assertions.assertEquals(10L, order.getId());
        Assertions.assertEquals("Bruno Dias", order.getCustomerName());
        Assertions.assertEquals("Teclado Gamer", order.getProductName());
        Assertions.assertEquals(3, order.getQuantity());
        Assertions.assertEquals(150.0, order.getPrice());
        Assertions.assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    public void testOrderEventModel() {
        OrderEvent event = OrderEvent.builder()
                .id(1L)
                .customerName("Ana Clara")
                .productName("Cadeira Ergonômica")
                .quantity(1)
                .price(890.0)
                .status(OrderStatus.APPROVED)
                .timestamp(123456789L)
                .build();

        Assertions.assertEquals(1L, event.getId());
        Assertions.assertEquals("Ana Clara", event.getCustomerName());
        Assertions.assertEquals("Cadeira Ergonômica", event.getProductName());
        Assertions.assertEquals(1, event.getQuantity());
        Assertions.assertEquals(890.0, event.getPrice());
        Assertions.assertEquals(OrderStatus.APPROVED, event.getStatus());
        Assertions.assertEquals(123456789L, event.getTimestamp());
    }

    @Test
    public void testPaymentEventModel() {
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .orderId(55L)
                .status("CONFIRMED")
                .paymentId("pay-999")
                .build();

        Assertions.assertEquals(55L, paymentEvent.getOrderId());
        Assertions.assertEquals("CONFIRMED", paymentEvent.getStatus());
        Assertions.assertEquals("pay-999", paymentEvent.getPaymentId());
    }

    @Test
    public void testOrderStatusEnum() {
        Assertions.assertEquals(3, OrderStatus.values().length);
        Assertions.assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        Assertions.assertEquals(OrderStatus.APPROVED, OrderStatus.valueOf("APPROVED"));
        Assertions.assertEquals(OrderStatus.REJECTED, OrderStatus.valueOf("REJECTED"));
    }
}
