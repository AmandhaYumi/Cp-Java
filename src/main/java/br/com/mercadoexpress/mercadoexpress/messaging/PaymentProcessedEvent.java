package br.com.mercadoexpress.mercadoexpress.messaging;

public record PaymentProcessedEvent(Long purchaseId, boolean approved, String transactionId,
                                    String message) {}
