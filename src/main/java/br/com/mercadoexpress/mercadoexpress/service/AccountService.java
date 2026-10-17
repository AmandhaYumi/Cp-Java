package br.com.mercadoexpress.mercadoexpress.service;

import br.com.mercadoexpress.mercadoexpress.domain.Role;
import br.com.mercadoexpress.mercadoexpress.domain.UserAccount;
import br.com.mercadoexpress.mercadoexpress.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserAccount register(String name, String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }
        return repository.save(new UserAccount(name.trim(), normalizedEmail,
                passwordEncoder.encode(password), Role.CLIENTE));
    }

    @Transactional(readOnly = true)
    public UserAccount byEmail(String email) {
        return repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = byEmail(username);
        return User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .roles(account.getRole().name())
                .build();
    }
}
