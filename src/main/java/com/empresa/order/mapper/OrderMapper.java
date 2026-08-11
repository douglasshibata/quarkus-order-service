package com.empresa.order.mapper;

import com.empresa.order.domain.Order;
import com.empresa.order.dto.OrderCreateDTO;
import com.empresa.order.dto.OrderResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Order toEntity(OrderCreateDTO createDTO);

    OrderResponseDTO toResponseDTO(Order order);
}
