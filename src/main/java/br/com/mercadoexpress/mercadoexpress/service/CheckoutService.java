package br.com.mercadoexpress.mercadoexpress.service;

import br.com.mercadoexpress.mercadoexpress.domain.*;
import br.com.mercadoexpress.mercadoexpress.messaging.*;
import br.com.mercadoexpress.mercadoexpress.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final ProductService products;
    private final AccountService accounts;
    private final PurchaseRepository purchases;
    private final ApplicationEventPublisher events;

    @Transactional
    public Purchase checkout(String email, Map<Long, Integer> quantities,
                             String cardHolder, String cardNumber) {
        if (quantities == null || quantities.isEmpty()) throw new IllegalArgumentException("O carrinho está vazio.");
        Purchase purchase = new Purchase();
        purchase.setCustomer(accounts.byEmail(email));
        quantities.forEach((productId, quantity) -> {
            if (quantity == null || quantity < 1) return;
            Product product = products.get(productId);
            if (!product.isActive() || product.getStock() < quantity) {
                throw new IllegalArgumentException("Estoque insuficiente para " + product.getName());
            }
            product.setStock(product.getStock() - quantity);
            purchase.addItem(new PurchaseItem(product, quantity));
        });
        if (purchase.getItems().isEmpty()) throw new IllegalArgumentException("O carrinho está vazio.");
        purchases.saveAndFlush(purchase);
        events.publishEvent(new PaymentRequestedEvent(purchase.getId(), purchase.getTotal(),
                email, cardHolder, cardNumber));
        return purchase;
    }
}
