package br.com.mercadoexpress.mercadoexpress.bank;

public record BankPaymentResponse(boolean approved, String transactionId, String message) {}
