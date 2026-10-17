package br.com.mercadoexpress.mercadoexpress.service;

import br.com.mercadoexpress.mercadoexpress.domain.*;
import br.com.mercadoexpress.mercadoexpress.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final PurchaseRepository purchases;
    private final ProductRepository products;
    private final EmailLogRepository emails;

    public record CategoryStat(String category, BigDecimal revenue, int percent) {}
    public record Dashboard(long orders, BigDecimal revenue, BigDecimal profit, long stock,
                            long sentEmails, List<CategoryStat> categories, List<Purchase> recent) {}

    @Transactional(readOnly = true)
    public Dashboard load() {
        List<Purchase> approved = purchases.findByStatus(PurchaseStatus.APROVADA);
        BigDecimal revenue = approved.stream().map(Purchase::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal profit = approved.stream().map(Purchase::getProfit).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> byCategory = new TreeMap<>();
        approved.stream().flatMap(p -> p.getItems().stream()).forEach(item ->
                byCategory.merge(item.getProduct().getCategory(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())), BigDecimal::add));
        BigDecimal max = byCategory.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
        List<CategoryStat> categories = byCategory.entrySet().stream()
                .map(e -> new CategoryStat(e.getKey(), e.getValue(), max.signum() == 0 ? 0
                        : e.getValue().multiply(BigDecimal.valueOf(100)).divide(max, 0, java.math.RoundingMode.HALF_UP).intValue()))
                .toList();
        long stock = products.findAll().stream().mapToLong(Product::getStock).sum();
        List<Purchase> recent = purchases.findAllByOrderByCreatedAtDesc().stream().limit(8).toList();
        return new Dashboard(approved.size(), revenue, profit, stock,
                emails.countByStatus("ENVIADO"), categories, recent);
    }
}
