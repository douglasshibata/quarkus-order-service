package com.empresa.order.producer;

import com.empresa.order.domain.OrderEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderEventProducer {

    private static final Logger LOG = Logger.getLogger(OrderEventProducer.class);

    @Inject
    @Channel("orders-out")
    Emitter<OrderEvent> orderEventEmitter;

    public void sendOrderEvent(OrderEvent event) {
        LOG.infof("Disparando evento de pedido para o Kafka (canal 'orders-out'): %s", event);
        try {
            orderEventEmitter.send(event);
            LOG.infof("Evento do pedido %d publicado no canal reativo", event.getId());
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao emitir evento de pedido %d", event.getId());
        }
    }
}
