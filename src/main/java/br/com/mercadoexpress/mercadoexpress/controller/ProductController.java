package br.com.mercadoexpress.mercadoexpress.controller;

import br.com.mercadoexpress.mercadoexpress.domain.Product;
import br.com.mercadoexpress.mercadoexpress.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService products;

    @GetMapping
    String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("products", products.list(search));
        model.addAttribute("search", search);
        return "produtos/lista";
    }

    @GetMapping("/new")
    String create(Model model) { model.addAttribute("product", new Product()); return "produtos/formulario"; }

    @PostMapping
    String create(@Valid @ModelAttribute Product product, BindingResult binding) {
        if (binding.hasErrors()) return "produtos/formulario";
        products.save(product);
        return "redirect:/products?saved";
    }

    @GetMapping("/{id}/edit")
    String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", products.get(id));
        return "produtos/formulario";
    }

    @PostMapping("/{id}")
    String update(@PathVariable Long id, @Valid @ModelAttribute Product product, BindingResult binding) {
        if (binding.hasErrors()) return "produtos/formulario";
        products.update(id, product);
        return "redirect:/products?saved";
    }

    @PostMapping("/{id}/delete")
    String delete(@PathVariable Long id, RedirectAttributes redirect) {
        products.delete(id);
        redirect.addFlashAttribute("message", "Produto removido do catálogo.");
        return "redirect:/products";
    }
}
