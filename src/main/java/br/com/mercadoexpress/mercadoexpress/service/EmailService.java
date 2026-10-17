package br.com.mercadoexpress.mercadoexpress.service;

import br.com.mercadoexpress.mercadoexpress.domain.*;
import br.com.mercadoexpress.mercadoexpress.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailLogRepository logs;

    @Value("${app.mail.from}") private String from;

    public void sendPaymentConfirmation(Purchase purchase) {
        String subject = "Mercadinho - pagamento confirmado #" + purchase.getId();
        String body = "Olá, " + purchase.getCustomer().getName() + "!\n\n"
                + "Seu pagamento de R$ " + purchase.getTotal() + " foi aprovado.\n"
                + "Transação Banco Tranquilo: " + purchase.getBankTransactionId() + "\n\n"
                + "Obrigado por comprar no Mercadinho.";
        String status = "ENVIADO";
        String details = "Mensagem entregue ao servidor SMTP";
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(purchase.getCustomer().getEmail());
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException ex) {
            status = "FALHA";
            details = ex.getMostSpecificCause().getMessage();
        }
        logs.save(new EmailLog(purchase, purchase.getCustomer().getEmail(), subject, status, details));
    }
}
