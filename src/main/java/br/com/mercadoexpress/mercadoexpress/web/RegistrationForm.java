package br.com.mercadoexpress.mercadoexpress.web;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationForm {
    @NotBlank(message = "Informe seu nome") @Size(max = 100)
    private String name;
    @NotBlank(message = "Informe seu e-mail") @Email(message = "Informe um e-mail válido")
    private String email;
    @NotBlank(message = "Informe uma senha") @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    private String password;
}
