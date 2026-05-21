package com.ecoterminal.controller;

import com.ecoterminal.dto.EmissaoResultadoDTO;
import com.ecoterminal.model.AlertaEmissao;
import com.ecoterminal.service.AlertaService;
import com.ecoterminal.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardApiController {

    private final RelatorioService relatorioService;
    private final AlertaService    alertaService;

    public DashboardApiController(RelatorioService relatorioService,
                                  AlertaService alertaService) {
        this.relatorioService = relatorioService;
        this.alertaService    = alertaService;
    }

    // ── Resumo e relatórios ──────
    @GetMapping("/terminal/{terminalId}")
    public ResponseEntity<RelatorioService.DashboardResumo> resumoTerminal(
            @PathVariable Long terminalId,
            @RequestParam(defaultValue = "0") int ano) {
        return ResponseEntity.ok(
                relatorioService.resumoTerminal(terminalId, resolverAno(ano)));
    }
    @GetMapping("/terminal/{terminalId}/maiores-emissores")
    public ResponseEntity<List<EmissaoResultadoDTO>> maioresEmissores(
            @PathVariable Long terminalId,
            @RequestParam(defaultValue = "0")  int ano,
            @RequestParam(defaultValue = "10") int top) {
        return ResponseEntity.ok(
                relatorioService.maioresEmissores(terminalId, resolverAno(ano), top));
    }

    /**
     * Distribuição de CO2 por padrão de motor 
     */
    @GetMapping("/terminal/{terminalId}/co2-por-motor")
    public ResponseEntity<Map<String, Double>> co2PorPadraoMotor(
            @PathVariable Long terminalId,
            @RequestParam(defaultValue = "0") int ano) {
        return ResponseEntity.ok(
                relatorioService.co2PorPadraoMotor(terminalId, resolverAno(ano)));
    }

    /**
     * Relatório anual completo por ônibus (obrigação Lei 16.802).
     * Recalcula ônibus sem resultado no ano solicitado.
     */
    @GetMapping("/terminal/{terminalId}/relatorio-anual")
    public ResponseEntity<List<EmissaoResultadoDTO>> relatorioAnual(
            @PathVariable Long terminalId,
            @RequestParam(defaultValue = "0") int ano) {
        return ResponseEntity.ok(
                relatorioService.relatorioAnualTerminal(terminalId, resolverAno(ano)));
    }

    /**
     * Histórico de emissões de um ônibus (todos os anos disponíveis).
     */
    @GetMapping("/onibus/{onibusId}/historico")
    public ResponseEntity<List<EmissaoResultadoDTO>> historicoOnibus(
            @PathVariable Long onibusId) {
        return ResponseEntity.ok(relatorioService.historicoOnibus(onibusId));
    }

    // ── Alertas ──────
    @GetMapping("/alertas/terminal/{terminalId}")
    public ResponseEntity<List<AlertaEmissao>> alertasPorTerminal(
            @PathVariable Long terminalId) {
        return ResponseEntity.ok(alertaService.listarPorTerminal(terminalId));
    }
    @GetMapping("/alertas/onibus/{onibusId}")
    public ResponseEntity<List<AlertaEmissao>> alertasPorOnibus(
            @PathVariable Long onibusId) {
        return ResponseEntity.ok(alertaService.listarPorOnibus(onibusId));
    }
    @GetMapping("/alertas/terminal/{terminalId}/count")
    public ResponseEntity<Long> contarAlertasNaoReconhecidos(
            @PathVariable Long terminalId) {
        return ResponseEntity.ok(alertaService.contarNaoReconhecidos(terminalId));
    }
    @PatchMapping("/alertas/{alertaId}/reconhecer")
    public ResponseEntity<AlertaEmissao> reconhecerAlerta(
            @PathVariable Long alertaId) {
        return ResponseEntity.ok(alertaService.reconhecer(alertaId));
    }
   
    // ── Helper ────────
    private static int resolverAno(int ano) {
        return (ano == 0) ? Year.now().getValue() : ano;
    }
}