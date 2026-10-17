package br.com.mercadoexpress.mercadoexpress.service;

import br.com.mercadoexpress.mercadoexpress.domain.Product;
import br.com.mercadoexpress.mercadoexpress.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    @Transactional(readOnly = true)
    public List<Product> list(String search) {
        if (search == null || search.isBlank()) {
            return repository.findByActiveTrueOrderByNameAsc();
        }
        return repository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrderByNameAsc(search, search);
    }

    @Transactional(readOnly = true)
    public Product get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }

    @Transactional
    public Product save(Product product) {
        return repository.save(product);
    }

    @Transactional
    public Product update(Long id, Product values) {
        Product product = get(id);
        product.setName(values.getName());
        product.setCategory(values.getCategory());
        product.setDescription(values.getDescription());
        product.setPrice(values.getPrice());
        product.setCostPrice(values.getCostPrice());
        product.setStock(values.getStock());
        product.setActive(values.isActive());
        return product;
    }

    @Transactional
    public void delete(Long id) {
        Product product = get(id);
        product.setActive(false);
    }
}
