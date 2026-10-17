package br.com.bancotranquilo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootApplication
@RestController
public class BancoTranquiloApplication {
    public static void main(String[] args) {
        SpringApplication.run(BancoTranquiloApplication.class, args);
    }

    public record PaymentRequest(@NotNull Long purchaseId, @NotNull @DecimalMin("0.01") BigDecimal amount,
                                 @NotBlank String cardHolder, @NotBlank String cardNumber) {}
    public record PaymentResponse(boolean approved, String transactionId, String message) {}

    @PostMapping("/api/banco-tranquilo/pagamentos")
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request) {
        String digits = request.cardNumber().replaceAll("\\D", "");
        boolean approved = digits.length() >= 13 && digits.length() <= 19 && !digits.endsWith("0000");
        return new PaymentResponse(approved, UUID.randomUUID().toString(), approved
                ? "Pagamento autorizado pelo Banco Tranquilo (simulador)"
                : "Pagamento recusado pelo Banco Tranquilo (simulador)");
    }
}
