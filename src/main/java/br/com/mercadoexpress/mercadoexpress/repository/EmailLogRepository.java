package br.com.mercadoexpress.mercadoexpress.repository;

import br.com.mercadoexpress.mercadoexpress.domain.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    long countByStatus(String status);
}
