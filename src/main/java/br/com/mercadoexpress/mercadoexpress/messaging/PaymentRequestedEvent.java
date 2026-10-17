package br.com.mercadoexpress.mercadoexpress.messaging;

import java.math.BigDecimal;

public record PaymentRequestedEvent(Long purchaseId, BigDecimal amount, String customerEmail,
                                    String cardHolder, String cardNumber) {}
