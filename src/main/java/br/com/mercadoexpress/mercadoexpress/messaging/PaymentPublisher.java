package br.com.mercadoexpress.mercadoexpress.messaging;

public interface PaymentPublisher {
    void publish(PaymentRequestedEvent event);
}
