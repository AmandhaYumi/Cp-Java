package br.com.mercadoexpress.mercadoexpress.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ApiCheckoutRequest(
        @NotEmpty List<@Valid Item> items,
        @NotBlank String cardHolder,
        @NotBlank String cardNumber
) {
    public record Item(@NotNull Long productId, @Min(1) int quantity) {}
}
