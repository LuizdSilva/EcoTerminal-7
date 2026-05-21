package com.ecoterminal.controller;

import com.ecoterminal.model.LeituraCO2;
import com.ecoterminal.model.Onibus;
import com.ecoterminal.repository.LeituraCO2Repository;
import com.ecoterminal.repository.OnibusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class LeituraMvcController {

    private final LeituraCO2Repository leituraRepository;
    private final OnibusRepository     onibusRepository;
    // ── Lista de leituras (últimas 24h) ───
    @GetMapping("/leituras")
    public String listar(Model model) {
        LocalDateTime inicio = LocalDateTime.now().minusHours(24);
        List<LeituraCO2> leituras = leituraRepository.findAll().stream()
                .filter(l -> l.getDataHora() != null && l.getDataHora().isAfter(inicio))
                .sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora()))
                .toList();

        model.addAttribute("leituras", leituras);
        return "leituras";
    }
    // ── Formulário nova leitura ─────
    @GetMapping("/leituras/nova")
    public String novaForm(Model model) {
        model.addAttribute("leitura",  new LeituraCO2());
        model.addAttribute("onibus",   onibusRepository.findAll());
        return "leitura-form";
    }
    // ── Salvar nova leitura ───────
    @PostMapping("/leituras/nova")
    public String salvar(@ModelAttribute LeituraCO2 leitura,
                         @RequestParam Long onibusId,
                         RedirectAttributes ra) {
        Onibus onibus = onibusRepository.findById(onibusId)
                .orElseThrow(() -> new IllegalArgumentException("Ônibus não encontrado"));
        leitura.setOnibus(onibus);
        if (leitura.getDataHora() == null) {
            leitura.setDataHora(LocalDateTime.now());
        }
        leituraRepository.save(leitura);
        ra.addFlashAttribute("sucesso", "Leitura registrada com sucesso!");
        return "redirect:/leituras";
    }

    // ── Excluir leitura ────
    @PostMapping("/leituras/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        leituraRepository.deleteById(id);
        ra.addFlashAttribute("sucesso", "Leitura removida.");
        return "redirect:/leituras";
    }
}