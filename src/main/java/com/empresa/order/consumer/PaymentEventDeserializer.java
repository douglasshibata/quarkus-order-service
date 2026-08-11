package com.empresa.order.consumer;

import com.empresa.order.domain.PaymentEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PaymentEventDeserializer extends ObjectMapperDeserializer<PaymentEvent> {
    public PaymentEventDeserializer() {
        super(PaymentEvent.class);
    }
}
