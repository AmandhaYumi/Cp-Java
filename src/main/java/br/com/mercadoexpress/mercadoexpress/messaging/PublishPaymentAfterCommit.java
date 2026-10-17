package br.com.mercadoexpress.mercadoexpress.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PublishPaymentAfterCommit {
    private final PaymentPublisher payments;

    // O consumidor so recebe a mensagem depois que a compra existe no banco.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(PaymentRequestedEvent event) {
        payments.publish(event);
    }
}
