package com.empresa.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long id;
    private String customerName;
    private String productName;
    private Integer quantity;
    private Double price;
    private OrderStatus status;
    private Long timestamp;
}
