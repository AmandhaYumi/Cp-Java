package br.com.mercadoexpress.mercadoexpress.service;

import br.com.mercadoexpress.mercadoexpress.domain.*;
import br.com.mercadoexpress.mercadoexpress.messaging.PaymentProcessedEvent;
import br.com.mercadoexpress.mercadoexpress.repository.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service
@RequiredArgsConstructor
public class PaymentResponseHandler {
    private final PurchaseRepository purchases;
    private final EmailService emailService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(PaymentProcessedEvent event) {
        Purchase purchase = purchases.findById(event.purchaseId())
                .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada: " + event.purchaseId()));
        if (purchase.getStatus() != PurchaseStatus.PENDENTE) return;
        purchase.setBankTransactionId(event.transactionId());
        purchase.setStatus(event.approved() ? PurchaseStatus.APROVADA : PurchaseStatus.RECUSADA);
        if (!event.approved()) {
            purchase.getItems().forEach(item ->
                    item.getProduct().setStock(item.getProduct().getStock() + item.getQuantity()));
        } else {
            emailService.sendPaymentConfirmation(purchase);
        }
    }
}
