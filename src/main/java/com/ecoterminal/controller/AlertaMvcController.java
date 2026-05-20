package com.ecoterminal.controller;

import com.ecoterminal.model.AlertaEmissao;
import com.ecoterminal.repository.AlertaEmissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AlertaMvcController {

    private final AlertaEmissaoRepository alertaRepository;

    // ── Lista de alertas não reconhecidos ────────────────────────────────────
    @GetMapping("/alertas")
    public String listar(Model model) {
        List<AlertaEmissao> alerts = alertaRepository.findAll().stream()
                .filter(a -> !a.isReconhecido())
                .sorted(Comparator
                        .comparing(AlertaEmissao::getSeveridade).reversed()
                        .thenComparing(AlertaEmissao::getDataHora).reversed())
                .toList();

        model.addAttribute("alerts", alerts);
        return "alerts";
    }

    // ── Reconhecer alerta ────────────────────────────────────────────────────
    @PostMapping("/alertas/{id}/reconhecer")
    public String reconhecer(@PathVariable Long id, RedirectAttributes ra) {
        alertaRepository.findById(id).ifPresent(a -> {
            a.setReconhecido(true);
            alertaRepository.save(a);
        });
        ra.addFlashAttribute("sucesso", "Alerta reconhecido.");
        return "redirect:/alertas";
    }
}