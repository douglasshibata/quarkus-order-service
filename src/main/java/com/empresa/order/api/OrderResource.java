package com.empresa.order.api;

import com.empresa.order.domain.Order;
import com.empresa.order.domain.OrderEvent;
import com.empresa.order.domain.OrderStatus;
import com.empresa.order.dto.OrderCreateDTO;
import com.empresa.order.mapper.OrderMapper;
import com.empresa.order.producer.OrderEventProducer;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private static final Logger LOG = Logger.getLogger(OrderResource.class);

    @Inject
    OrderMapper orderMapper;

    @Inject
    OrderEventProducer orderEventProducer;

    @POST
    public Uni<Response> create(OrderCreateDTO createDTO) {
        LOG.infof("Recebida requisição de criação de pedido para o cliente: %s", createDTO.getCustomerName());

        Order order = orderMapper.toEntity(createDTO);
        order.setStatus(OrderStatus.PENDING);

        return Panache.withTransaction(order::persist)
            .map(ignored -> {
                LOG.infof("Pedido %d persistido com sucesso no banco de dados.", order.getId());

                // Disparar evento reativo após persistência bem-sucedida
                OrderEvent event = OrderEvent.builder()
                        .id(order.getId())
                        .customerName(order.getCustomerName())
                        .productName(order.getProductName())
                        .quantity(order.getQuantity())
                        .price(order.getPrice())
                        .status(order.getStatus())
                        .timestamp(System.currentTimeMillis())
                        .build();

                orderEventProducer.sendOrderEvent(event);

                return Response.status(Response.Status.CREATED)
                        .entity(orderMapper.toResponseDTO(order))
                        .build();
            });
    }

    @GET
    @Path("/{id}")
    public Uni<Response> getById(@PathParam("id") Long id) {
        LOG.infof("Buscando pedido com id: %d", id);
        return Order.<Order>findById(id)
                .onItem().ifNotNull().transform(order -> Response.ok(orderMapper.toResponseDTO(order)).build())
                .onItem().ifNull().continueWith(() -> {
                    LOG.warnf("Pedido %d não encontrado.", id);
                    return Response.status(Response.Status.NOT_FOUND).build();
                });
    }
}
