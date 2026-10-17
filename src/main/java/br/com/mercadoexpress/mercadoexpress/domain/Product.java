package br.com.mercadoexpress.mercadoexpress.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TDS_SEC_MVC_TB_MERCADO")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do produto")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank(message = "Informe a categoria")
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String category;

    @Size(max = 600)
    @Column(length = 600)
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "cost_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @NotNull
    @Min(value = 0, message = "O estoque não pode ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private boolean active = true;

    public Product(String name, String category, String description, BigDecimal price,
                   BigDecimal costPrice, Integer stock) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
        this.active = true;
    }
}
