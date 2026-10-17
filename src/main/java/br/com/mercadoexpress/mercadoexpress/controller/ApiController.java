package br.com.mercadoexpress.mercadoexpress.controller;

import br.com.mercadoexpress.mercadoexpress.domain.*;
import br.com.mercadoexpress.mercadoexpress.service.*;
import br.com.mercadoexpress.mercadoexpress.web.ApiCheckoutRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final ProductService products;
    private final CheckoutService checkout;
    public record CheckoutResponse(Long purchaseId, PurchaseStatus status, BigDecimal total) {}

    @GetMapping("/products")
    public Object products(@RequestParam(required = false) String search) {
        return products.list(search);
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product product) { return products.save(product); }

    @PutMapping("/products/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product product) {
        return products.update(id, product);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { products.delete(id); }

    @PostMapping("/purchases")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CheckoutResponse checkout(@Valid @RequestBody ApiCheckoutRequest request, Authentication auth) {
        var quantities = new LinkedHashMap<Long, Integer>();
        request.items().forEach(item -> quantities.merge(item.productId(), item.quantity(), Integer::sum));
        Purchase purchase = checkout.checkout(auth.getName(), quantities, request.cardHolder(), request.cardNumber());
        return new CheckoutResponse(purchase.getId(), purchase.getStatus(), purchase.getTotal());
    }
}
