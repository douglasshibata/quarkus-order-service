package com.empresa.order.consumer;

import com.empresa.order.domain.Order;
import com.empresa.order.domain.OrderStatus;
import com.empresa.order.domain.PaymentEvent;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PaymentEventConsumer {

    private static final Logger LOG = Logger.getLogger(PaymentEventConsumer.class);

    @Incoming("payments-in")
    @Retry(maxRetries = 3, delay = 1000)
    @Fallback(fallbackMethod = "handlePaymentFallback")
    public Uni<Void> consume(PaymentEvent paymentEvent) {
        LOG.infof("Recebido evento de pagamento do Kafka: %s", paymentEvent);

        if (paymentEvent == null || paymentEvent.getOrderId() == null) {
            LOG.warn("Recebido PaymentEvent nulo ou sem ID de pedido válido.");
            return Uni.createFrom().voidItem();
        }

        return Panache.withTransaction(() ->
            Order.<Order>findById(paymentEvent.getOrderId())
                .onItem().transformToUni(order -> {
                    if (order == null) {
                        LOG.warnf("Pedido %d não localizado no banco de dados.", paymentEvent.getOrderId());
                        return Uni.createFrom().voidItem();
                    }
                    if ("CONFIRMED".equalsIgnoreCase(paymentEvent.getStatus())) {
                        order.setStatus(OrderStatus.APPROVED);
                    } else {
                        order.setStatus(OrderStatus.REJECTED);
                    }
                    LOG.infof("Status do Pedido %d atualizado reativamente para %s após confirmação de pagamento",
                            order.getId(), order.getStatus());
                    return order.persist().replaceWithVoid();
                })
        ).replaceWithVoid();
    }

    public Uni<Void> handlePaymentFallback(PaymentEvent paymentEvent, Throwable throwable) {
        LOG.errorf(throwable, "Resiliência ativada: Falha esgotada ao processar pagamento do pedido %s. Registrando fallback...",
                paymentEvent != null ? paymentEvent.getOrderId() : "N/A");
        return Uni.createFrom().voidItem();
    }
}
