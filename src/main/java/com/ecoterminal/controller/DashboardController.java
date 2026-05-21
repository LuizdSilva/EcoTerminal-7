package com.ecoterminal.controller;

import com.ecoterminal.model.LeituraCO2;
import com.ecoterminal.repository.AlertaEmissaoRepository;
import com.ecoterminal.repository.LeituraCO2Repository;
import com.ecoterminal.repository.OnibusRepository;
import com.ecoterminal.repository.TerminalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.*;
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final TerminalRepository      terminalRepository;
    private final OnibusRepository        onibusRepository;
    private final LeituraCO2Repository    leituraRepository;
    private final AlertaEmissaoRepository alertaRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // ── Contagens gerais ──────
        long totalOnibus    = onibusRepository.count();
        long totalTerminais = terminalRepository.count();

        // Alertas não reconhecidos 
        long alertasAtivos = alertaRepository.findAll().stream()
                .filter(a -> !a.isReconhecido())
                .count();

        // ── Leituras das últimas 24h ──────
        LocalDateTime inicio24h = LocalDateTime.now().minusHours(24);
        List<LeituraCO2> todasLeituras = leituraRepository.findAll();

        List<LeituraCO2> leituras24h = todasLeituras.stream()
                .filter(l -> l.getDataHora() != null && l.getDataHora().isAfter(inicio24h))
                .sorted(Comparator.comparing(LeituraCO2::getDataHora).reversed())
                .toList();

        long leiturasHoje = leituras24h.size();

        // ── CO2 médio das últimas 24h ─────────
        double kmMedio24h = leituras24h.stream()
                .mapToDouble(LeituraCO2::getKmPercorridos)
                .average()
                .orElse(0.0);

        // ── KPIs ────
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("co2TotalHoje",   String.format("%.1f", kmMedio24h));
        kpis.put("alertasAtivos",  alertasAtivos);
        kpis.put("totalOnibus",    totalOnibus);
        kpis.put("totalTerminais", totalTerminais);
        kpis.put("co2Medio24h",    String.format("%.1f", kmMedio24h));
        kpis.put("leiturasHoje",   leiturasHoje);

        // ── Status da frota (até 10 ônibus) ───
        List<Map<String, Object>> statusOnibus = new ArrayList<>();
        onibusRepository.findAll().stream().limit(10).forEach(o -> {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("placa",        o.getPrefixo());
            s.put("tipo",         o.getTipo()        != null ? o.getTipo().toString()        : "—");
            s.put("padrao",       o.getPadraoMotor() != null ? o.getPadraoMotor().toString() : "—");
            s.put("terminal",     o.getTerminal()    != null ? o.getTerminal().getNome()     : "—");
            s.put("ultimoCo2",    null);
            s.put("ultimaLeitura", null);
            statusOnibus.add(s);
        });

        // ── Leituras recentes (últimas 10) ────
        List<LeituraCO2> leiturasRecentes = leituras24h.stream()
                .limit(10)
                .toList();

        // ── co2PorHora para o gráfico JS ─────
        Map<Integer, Double> co2PorHora = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) co2PorHora.put(i, 0.0);
        leituras24h.forEach(l -> {
            int hora = l.getDataHora().getHour();
            co2PorHora.merge(hora, l.getKmPercorridos(), Double::sum);
        });

        // ── Passa tudo para o template ──────
        model.addAttribute("kpis",            kpis);
        model.addAttribute("statusOnibus",     statusOnibus);
        model.addAttribute("leiturasRecentes", leiturasRecentes);
        model.addAttribute("co2PorHora",       co2PorHora);
        model.addAttribute("terminais",        terminalRepository.findAll());

        return "dashboard";
    }
}