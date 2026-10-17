package br.com.mercadoexpress.mercadoexpress.config;

import br.com.mercadoexpress.mercadoexpress.domain.*;
import br.com.mercadoexpress.mercadoexpress.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
                    new Product("Cesta de frutas", "Hortifruti", "Banana, maçã, laranja e mamão.", new BigDecimal("39.90"), new BigDecimal("24.50"), 30),
                    new Product("Cesta de café da manhã", "Mercearia", "Itens para o café da manhã.", new BigDecimal("54.90"), new BigDecimal("33.00"), 20),
                    new Product("Kit de limpeza", "Limpeza", "Detergente, desinfetante, esponja e pano multiuso.", new BigDecimal("47.50"), new BigDecimal("28.20"), 25),
                    new Product("Meias de algodão", "Vestuário", "Seis pares de meias de algodão.", new BigDecimal("36.00"), new BigDecimal("17.80"), 40)
            ));
        }
        Map<String, String> previousNames = Map.of(
                "Kit de frutas frescas", "Cesta de frutas",
                "Cesta café da manhã", "Cesta de café da manhã",
                "Combo limpeza express", "Kit de limpeza",
                "Pack meias confort", "Meias de algodão");
        products.findAll().forEach(product -> {
            String name = previousNames.get(product.getName());
            if (name != null) {
                product.setName(name);
                products.save(product);
            }
        });
    }
}
