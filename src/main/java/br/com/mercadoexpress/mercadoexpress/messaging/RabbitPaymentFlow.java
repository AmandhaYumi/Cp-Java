package br.com.mercadoexpress.mercadoexpress.messaging;

import br.com.mercadoexpress.mercadoexpress.bank.BancoTranquiloClient;
import br.com.mercadoexpress.mercadoexpress.config.RabbitConfig;
import br.com.mercadoexpress.mercadoexpress.service.PaymentResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class RabbitPaymentFlow implements PaymentPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final BancoTranquiloClient bankClient;
    private final PaymentResponseHandler responseHandler;

    @Override
    public void publish(PaymentRequestedEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.REQUEST_KEY, json(event));
    }

    @RabbitListener(queues = RabbitConfig.REQUEST_QUEUE)
    public void processRequest(String payload) throws JacksonException {
        PaymentRequestedEvent request = objectMapper.readValue(payload, PaymentRequestedEvent.class);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RESPONSE_KEY,
                json(bankClient.process(request)));
    }

    @RabbitListener(queues = RabbitConfig.RESPONSE_QUEUE)
    public void processResponse(String payload) throws JacksonException {
        responseHandler.handle(objectMapper.readValue(payload, PaymentProcessedEvent.class));
    }

    private String json(Object value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (JacksonException ex) { throw new IllegalArgumentException("Falha ao serializar mensagem", ex); }
    }
}
