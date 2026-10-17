package br.com.mercadoexpress.mercadoexpress.bank;

import br.com.mercadoexpress.mercadoexpress.messaging.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BancoTranquiloClient {
    private final RestClient restClient;

    public BancoTranquiloClient(@Value("${app.bank.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public PaymentProcessedEvent process(PaymentRequestedEvent event) {
        BankPaymentResponse response = restClient.post()
                .uri("/pagamentos")
                .body(new BankPaymentRequest(event.purchaseId(), event.amount(), event.cardHolder(), event.cardNumber()))
                .retrieve()
                .body(BankPaymentResponse.class);
        if (response == null) throw new IllegalStateException("Banco Tranquilo retornou resposta vazia");
        return new PaymentProcessedEvent(event.purchaseId(), response.approved(),
                response.transactionId(), response.message());
    }
}
