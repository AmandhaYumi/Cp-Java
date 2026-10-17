package br.com.mercadoexpress.mercadoexpress.controller;

import br.com.mercadoexpress.mercadoexpress.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final DashboardService dashboards;
    @GetMapping("/admin/dashboard")
    String dashboard(Model model) {
        model.addAttribute("dashboard", dashboards.load());
        return "admin/painel";
    }
}
