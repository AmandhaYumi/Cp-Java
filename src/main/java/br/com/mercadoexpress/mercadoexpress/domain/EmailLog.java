package br.com.mercadoexpress.mercadoexpress.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TDS_EMAIL_LOG_MERCADO")
@Getter
@Setter
@NoArgsConstructor
public class EmailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Purchase purchase;

    @Column(nullable = false, length = 160)
    private String recipient;

    @Column(nullable = false, length = 180)
    private String subject;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 800)
    private String details;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    public EmailLog(Purchase purchase, String recipient, String subject, String status, String details) {
        this.purchase = purchase;
        this.recipient = recipient;
        this.subject = subject;
        this.status = status;
        this.details = details;
    }
}
