package br.com.mercadoexpress.mercadoexpress;

import br.com.mercadoexpress.mercadoexpress.bank.BancoTranquiloClient;
import br.com.mercadoexpress.mercadoexpress.domain.*;
import br.com.mercadoexpress.mercadoexpress.messaging.*;
import br.com.mercadoexpress.mercadoexpress.repository.*;
import br.com.mercadoexpress.mercadoexpress.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "management.health.mail.enabled=false")
@ActiveProfiles("test")
class CheckoutFlowIntegrationTests {
    @Autowired CheckoutService checkout;
    @Autowired ProductRepository products;
    @Autowired PurchaseRepository purchases;
    @Autowired EmailLogRepository emails;
    @MockitoBean BancoTranquiloClient bank;
    @MockitoBean JavaMailSender mail;

    private Product product;

    @BeforeEach
    void product() {
        product = products.save(new Product("Produto de teste", "Teste", "Integracao",
                new BigDecimal("10.00"), new BigDecimal("6.00"), 5));
    }

    @Test
    void approvalAfterCommitPersistsStatusAndEmail() {
        when(bank.process(any())).thenAnswer(call -> {
            PaymentRequestedEvent event = call.getArgument(0);
            assertThat(purchases.existsById(event.purchaseId())).isTrue();
            return new PaymentProcessedEvent(event.purchaseId(), true, "TEST-APPROVED", "Aprovado");
        });
        Purchase created = checkout.checkout("cliente@mercadoexpress.com",
                Map.of(product.getId(), 2), "TESTE", "4111111111111111");
        Purchase persisted = purchases.findById(created.getId()).orElseThrow();
        assertThat(persisted.getStatus()).isEqualTo(PurchaseStatus.APROVADA);
        assertThat(persisted.getTotal()).isEqualByComparingTo("20.00");
        assertThat(products.findById(product.getId()).orElseThrow().getStock()).isEqualTo(3);
        assertThat(emails.findAll()).anySatisfy(log -> {
            assertThat(log.getRecipient()).isEqualTo("cliente@mercadoexpress.com");
            assertThat(log.getStatus()).isEqualTo("ENVIADO");
        });
        verify(mail).send(any(SimpleMailMessage.class));
    }

    @Test
    void refusalRestoresStockAndDoesNotSendEmail() {
        when(bank.process(any())).thenAnswer(call -> {
            PaymentRequestedEvent event = call.getArgument(0);
            return new PaymentProcessedEvent(event.purchaseId(), false, "TEST-DECLINED", "Recusado");
        });
        Purchase created = checkout.checkout("cliente@mercadoexpress.com",
                Map.of(product.getId(), 2), "TESTE", "4111111111110000");
        assertThat(purchases.findById(created.getId()).orElseThrow().getStatus())
                .isEqualTo(PurchaseStatus.RECUSADA);
        assertThat(products.findById(product.getId()).orElseThrow().getStock()).isEqualTo(5);
        verifyNoInteractions(mail);
    }

    @Test
    void insufficientStockRollsBackWithoutCallingBank() {
        long count = purchases.count();
        assertThatThrownBy(() -> checkout.checkout("cliente@mercadoexpress.com",
                Map.of(product.getId(), 6), "TESTE", "4111111111111111"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Estoque insuficiente");
        assertThat(purchases.count()).isEqualTo(count);
        assertThat(products.findById(product.getId()).orElseThrow().getStock()).isEqualTo(5);
        verifyNoInteractions(bank, mail);
    }
}
