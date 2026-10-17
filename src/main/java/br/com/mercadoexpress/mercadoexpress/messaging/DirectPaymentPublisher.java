package br.com.mercadoexpress.mercadoexpress.messaging;

import br.com.mercadoexpress.mercadoexpress.bank.BancoTranquiloClient;
import br.com.mercadoexpress.mercadoexpress.service.PaymentResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "false", matchIfMissing = true)
public class DirectPaymentPublisher implements PaymentPublisher {
    private final BancoTranquiloClient bankClient;
    private final PaymentResponseHandler responseHandler;

    @Override
    public void publish(PaymentRequestedEvent event) {
        responseHandler.handle(bankClient.process(event));
    }
}
