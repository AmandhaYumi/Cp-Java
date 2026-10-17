package br.com.mercadoexpress.mercadoexpress.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "mercado.pagamentos.exchange";
    public static final String REQUEST_QUEUE = "mercado.pagamentos.solicitacoes";
    public static final String RESPONSE_QUEUE = "mercado.pagamentos.respostas";
    public static final String REQUEST_KEY = "pagamento.solicitado";
    public static final String RESPONSE_KEY = "pagamento.processado";

    @Bean DirectExchange paymentExchange() { return new DirectExchange(EXCHANGE, true, false); }
    @Bean Queue paymentRequestQueue() { return QueueBuilder.durable(REQUEST_QUEUE).build(); }
    @Bean Queue paymentResponseQueue() { return QueueBuilder.durable(RESPONSE_QUEUE).build(); }

    @Bean
    Binding requestBinding(DirectExchange paymentExchange, Queue paymentRequestQueue) {
        return BindingBuilder.bind(paymentRequestQueue).to(paymentExchange).with(REQUEST_KEY);
    }

    @Bean
    Binding responseBinding(DirectExchange paymentExchange, Queue paymentResponseQueue) {
        return BindingBuilder.bind(paymentResponseQueue).to(paymentExchange).with(RESPONSE_KEY);
    }
}
