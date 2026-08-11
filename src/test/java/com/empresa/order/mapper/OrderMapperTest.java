package com.empresa.order.mapper;

import com.empresa.order.domain.Order;
import com.empresa.order.domain.OrderStatus;
import com.empresa.order.dto.OrderCreateDTO;
import com.empresa.order.dto.OrderResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    public void testToEntity() {
        OrderCreateDTO dto = OrderCreateDTO.builder()
                .customerName("Ana Silva")
                .productName("Notebook Pro")
                .quantity(2)
                .price(4500.00)
                .build();

        Order entity = orderMapper.toEntity(dto);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals("Ana Silva", entity.getCustomerName());
        Assertions.assertEquals("Notebook Pro", entity.getProductName());
        Assertions.assertEquals(2, entity.getQuantity());
        Assertions.assertEquals(4500.00, entity.getPrice());
    }

    @Test
    public void testToResponseDTO() {
        Order order = new Order();
        order.setId(123L);
        order.setCustomerName("Carlos Souza");
        order.setProductName("Teclado Mecânico");
        order.setQuantity(1);
        order.setPrice(350.0);
        order.setStatus(OrderStatus.PENDING);

        OrderResponseDTO dto = orderMapper.toResponseDTO(order);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(123L, dto.getId());
        Assertions.assertEquals("Carlos Souza", dto.getCustomerName());
        Assertions.assertEquals("Teclado Mecânico", dto.getProductName());
        Assertions.assertEquals(1, dto.getQuantity());
        Assertions.assertEquals(350.0, dto.getPrice());
        Assertions.assertEquals(OrderStatus.PENDING, dto.getStatus());
    }
}
