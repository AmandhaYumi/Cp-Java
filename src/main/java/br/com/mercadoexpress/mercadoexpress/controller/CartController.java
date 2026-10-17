package br.com.mercadoexpress.mercadoexpress.controller;

import br.com.mercadoexpress.mercadoexpress.domain.Product;
import br.com.mercadoexpress.mercadoexpress.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final ProductService products;
    private final CheckoutService checkout;
    public record CartLine(Product product, int quantity, BigDecimal subtotal) {}

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> cart(HttpSession session) {
        Object value = session.getAttribute("cart");
        if (value instanceof Map<?, ?>) return (Map<Long, Integer>) value;
        Map<Long, Integer> cart = new LinkedHashMap<>();
        session.setAttribute("cart", cart);
        return cart;
    }

    @PostMapping("/add/{id}")
    String add(@PathVariable Long id, @RequestParam(defaultValue = "1") int quantity,
               HttpSession session, RedirectAttributes redirect) {
        Product product = products.get(id);
        Map<Long, Integer> cart = cart(session);
        cart.put(id, Math.min(product.getStock(), cart.getOrDefault(id, 0) + Math.max(1, quantity)));
        redirect.addFlashAttribute("message", product.getName() + " adicionado ao carrinho.");
        return "redirect:/products";
    }

    @GetMapping
    String view(HttpSession session, Model model) {
        List<CartLine> lines = cart(session).entrySet().stream().map(e -> {
            Product product = products.get(e.getKey());
            return new CartLine(product, e.getValue(), product.getPrice().multiply(BigDecimal.valueOf(e.getValue())));
        }).toList();
        model.addAttribute("lines", lines);
        model.addAttribute("total", lines.stream().map(CartLine::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        return "cart";
    }

    @PostMapping("/remove/{id}")
    String remove(@PathVariable Long id, HttpSession session) { cart(session).remove(id); return "redirect:/cart"; }

    @PostMapping("/checkout")
    String checkout(@RequestParam String cardHolder, @RequestParam String cardNumber,
                    HttpSession session, Authentication auth, RedirectAttributes redirect) {
        try {
            var purchase = checkout.checkout(auth.getName(), new LinkedHashMap<>(cart(session)), cardHolder, cardNumber);
            cart(session).clear();
            redirect.addFlashAttribute("message", "Compra #" + purchase.getId() + " enviada para pagamento.");
            return "redirect:/purchases";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/cart";
        }
    }
}
