package br.com.mercadoexpress.mercadoexpress.bank;

import java.math.BigDecimal;

public record BankPaymentRequest(Long purchaseId, BigDecimal amount, String cardHolder,
                                 String cardNumber) {}
