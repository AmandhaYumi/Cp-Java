package br.com.mercadoexpress.mercadoexpress.repository;

import br.com.mercadoexpress.mercadoexpress.domain.Purchase;
import br.com.mercadoexpress.mercadoexpress.domain.PurchaseStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @EntityGraph(attributePaths = {"items", "customer"})
    List<Purchase> findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = {"items", "customer"})
    List<Purchase> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"items", "items.product", "customer"})
    List<Purchase> findByStatus(PurchaseStatus status);
}
