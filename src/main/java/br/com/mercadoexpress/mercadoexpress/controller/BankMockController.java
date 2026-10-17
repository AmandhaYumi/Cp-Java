package br.com.mercadoexpress.mercadoexpress.controller;

import br.com.mercadoexpress.mercadoexpress.bank.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/banco-tranquilo")
public class BankMockController {
    @PostMapping("/pagamentos")
    public ResponseEntity<BankPaymentResponse> pay(@RequestBody BankPaymentRequest request) {
        String digits = request.cardNumber() == null ? "" : request.cardNumber().replaceAll("\\D", "");
        boolean approved = request.amount() != null && request.amount().signum() > 0
                && digits.length() >= 4 && !digits.endsWith("0000");
        String message = approved ? "Pagamento autorizado pelo Banco Tranquilo"
                : "Pagamento recusado pelo Banco Tranquilo";
        return ResponseEntity.ok(new BankPaymentResponse(approved, UUID.randomUUID().toString(), message));
    }
}
