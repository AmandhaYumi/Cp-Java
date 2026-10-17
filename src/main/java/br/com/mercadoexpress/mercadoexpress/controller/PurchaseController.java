package br.com.mercadoexpress.mercadoexpress.controller;

import br.com.mercadoexpress.mercadoexpress.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseRepository purchases;
    @GetMapping("/purchases")
    String list(Authentication auth, Model model) {
        model.addAttribute("purchases", purchases.findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(auth.getName()));
        return "compras";
    }
}
