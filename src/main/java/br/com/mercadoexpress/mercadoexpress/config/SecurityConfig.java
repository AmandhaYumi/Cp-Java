package br.com.mercadoexpress.mercadoexpress.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/login", "/register", "/css/**", "/images/**", "/error", "/actuator/health").permitAll()
                        .requestMatchers("/api/banco-tranquilo/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("GERENTE")
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("GERENTE")
                        .requestMatchers(HttpMethod.GET, "/products/new", "/products/*/edit").hasRole("GERENTE")
                        .requestMatchers("/api/products/**").hasRole("GERENTE")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/products", true)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/?logout"))
                .httpBasic(basic -> {})
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        return http.build();
    }
}
