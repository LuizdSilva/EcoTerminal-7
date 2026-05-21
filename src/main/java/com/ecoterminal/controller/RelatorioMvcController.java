package com.ecoterminal.controller;

import com.ecoterminal.model.AlertaEmissao;
import com.ecoterminal.model.LeituraCO2;
import com.ecoterminal.repository.AlertaEmissaoRepository;
import com.ecoterminal.repository.LeituraCO2Repository;
import com.ecoterminal.repository.OnibusRepository;
import com.ecoterminal.service.RelatorioPdfService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller MVC — Relatórios de Auditoria
 *
 * GET  /relatorios          → página principal (template: reports.html)
 * GET  /relatorios/pdf?dias=N → download PDF
 */
@Controller
@RequiredArgsConstructor
public class RelatorioMvcController {

    private final LeituraCO2Repository    leituraRepository;
    private final AlertaEmissaoRepository alertaRepository;
    private final OnibusRepository        onibusRepository;
    private final RelatorioPdfService     relatorioPdfService;

    // ─── Página principal ────────────────────────────────────────────────────

    @GetMapping("/relatorios")
    public String paginaRelatorios(
            @RequestParam(defaultValue = "30") int dias,
            Model model) {

        LocalDateTime inicio = LocalDateTime.now().minusDays(dias);

        // Leituras no período — campo dataHora (correto no modelo)
        List<LeituraCO2> leituras = leituraRepository.findAll().stream()
                .filter(l -> l.getDataHora() != null && l.getDataHora().isAfter(inicio))
                .sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora()))
                .toList();

        // Alertas no período
        List<AlertaEmissao> alertas = alertaRepository.findAll().stream()
                .filter(a -> a.getDataHora() != null && a.getDataHora().isAfter(inicio))
                .sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora()))
                .toList();

        // KPIs — usando os campos reais do modelo LeituraCO2
        long   totalOnibus   = onibusRepository.count();
        long   totalAlertas  = alertas.size();
        long   totalLeituras = leituras.size();

        // NOx médio (campo noxPpm do modelo real)
        double noxMedio = leituras.stream()
                .filter(l -> l.getNoxPpm() != null)
                .mapToDouble(LeituraCO2::getNoxPpm)
                .average()
                .orElse(0.0);

        // Leituras com NOx acima de 5.0 ppm
        long leiturasAlerta = leituras.stream()
                .filter(l -> l.getNoxPpm() != null && l.getNoxPpm() > 5.0)
                .count();

        // KM total percorrido no período
        double kmTotal = leituras.stream()
                .mapToDouble(LeituraCO2::getKmPercorridos)
                .sum();

        model.addAttribute("diasSelecionado", dias);
        model.addAttribute("leituras", leituras);
        model.addAttribute("alertasPeriodo", alertas);
        model.addAttribute("totalOnibus", totalOnibus);
        model.addAttribute("totalAlertas", totalAlertas);
        model.addAttribute("totalLeituras", totalLeituras);
        model.addAttribute("noxMedio", String.format("%.2f", noxMedio));
        model.addAttribute("leiturasAlerta", leiturasAlerta);
        model.addAttribute("kmTotal", String.format("%.0f", kmTotal));
        model.addAttribute("currentUri", "/relatorios");

        return "reports";
    }

    // ─── Download PDF ────
    @GetMapping("/relatorios/pdf")
    public void downloadPdf(
            @RequestParam(defaultValue = "30") int dias,
            HttpServletResponse response) throws IOException {

        LocalDateTime inicio = LocalDateTime.now().minusDays(dias);

        List<LeituraCO2> leituras = leituraRepository.findAll().stream()
                .filter(l -> l.getDataHora() != null && l.getDataHora().isAfter(inicio))
                .sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora()))
                .toList();

        List<AlertaEmissao> alertas = alertaRepository.findAll().stream()
                .filter(a -> a.getDataHora() != null && a.getDataHora().isAfter(inicio))
                .sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora()))
                .toList();

        String nomeArquivo = "relatorio-ecoterminal-" + dias + "d-" + LocalDate.now() + ".pdf";

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");

        byte[] pdf = relatorioPdfService.gerarPdf(leituras, alertas, dias);
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }
}