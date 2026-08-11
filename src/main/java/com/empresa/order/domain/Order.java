package com.empresa.order.domain;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "customer_name", nullable = false)
    public String customerName;

    @Column(name = "product_name", nullable = false)
    public String productName;

    @Column(nullable = false)
    public Integer quantity;

    @Column(nullable = false)
    public Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OrderStatus status;
}
