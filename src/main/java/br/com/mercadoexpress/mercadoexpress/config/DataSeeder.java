package br.com.mercadoexpress.mercadoexpress.config;

import br.com.mercadoexpress.mercadoexpress.domain.*;
import br.com.mercadoexpress.mercadoexpress.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserAccountRepository users;
    private final ProductRepository products;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (!users.existsByEmailIgnoreCase("gerente@mercadoexpress.com")) {
            users.save(new UserAccount("Gerente Demo", "gerente@mercadoexpress.com",
                    encoder.encode("Gerente@123"), Role.GERENTE));
        }
        if (!users.existsByEmailIgnoreCase("cliente@mercadoexpress.com")) {
            users.save(new UserAccount("Cliente Demo", "cliente@mercadoexpress.com",
                    encoder.encode("Cliente@123"), Role.CLIENTE));
        }
        if (products.count() == 0) {
            products.saveAll(List.of(
                    new Product("Kit de frutas frescas", "Hortifruti", "Seleção com banana, maçã, laranja e mamão.", new BigDecimal("39.90"), new BigDecimal("24.50"), 30),
                    new Product("Cesta café da manhã", "Mercearia", "Itens essenciais para começar bem o dia.", new BigDecimal("54.90"), new BigDecimal("33.00"), 20),
                    new Product("Combo limpeza express", "Limpeza", "Detergente, desinfetante, esponja e pano multiuso.", new BigDecimal("47.50"), new BigDecimal("28.20"), 25),
                    new Product("Pack meias confort", "Vestuário", "Seis pares de meias de algodão.", new BigDecimal("36.00"), new BigDecimal("17.80"), 40)
            ));
        }
    }
}
