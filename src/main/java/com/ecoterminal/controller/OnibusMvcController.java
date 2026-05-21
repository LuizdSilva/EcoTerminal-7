package com.ecoterminal.controller;

import com.ecoterminal.enums.PadraoMotor;
import com.ecoterminal.enums.TipoCombustivel;
import com.ecoterminal.enums.TipoOnibus;
import com.ecoterminal.model.LeituraCO2;
import com.ecoterminal.model.Onibus;
import com.ecoterminal.model.Terminal;
import com.ecoterminal.repository.LeituraCO2Repository;
import com.ecoterminal.repository.OnibusRepository;
import com.ecoterminal.repository.TerminalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class OnibusMvcController {

    private final OnibusRepository onibusRepository;
    private final TerminalRepository  terminalRepository;
    private final LeituraCO2Repository leituraRepository;

    // ── Lista de ônibus ─────────
    @GetMapping("/onibus")
    public String listar(@RequestParam(required = false) Long terminalId, Model model) {
        List<Onibus> lista = (terminalId != null)
                ? onibusRepository.findByTerminalId(terminalId)
                : onibusRepository.findAll();

        Terminal terminalFiltro = (terminalId != null)
                ? terminalRepository.findById(terminalId).orElse(null)
                : null;

        model.addAttribute("onibus",    lista);
        model.addAttribute("terminais", terminalRepository.findAll());
        model.addAttribute("terminal",  terminalFiltro);
        return "onibus";
    }

    // ── Detalhe de um ônibus ─────
    @GetMapping("/onibus/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Onibus onibus = onibusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ônibus não encontrado: " + id));

        LocalDateTime inicio24h = LocalDateTime.now().minusHours(24);
        List<LeituraCO2> leituras = leituraRepository
                .findByOnibusIdAndDataHoraBetween(id, inicio24h, LocalDateTime.now());

        model.addAttribute("onibus",   onibus);
        model.addAttribute("leituras", leituras);
        return "onibus-detail";
    }

    // ── Formulário novo ônibus ───────
    @GetMapping("/onibus/novo")
    public String novoForm(Model model) {
        model.addAttribute("onibus",      new Onibus());
        model.addAttribute("terminais",   terminalRepository.findAll());
        model.addAttribute("tipos",       TipoOnibus.values());
        model.addAttribute("padroes",     PadraoMotor.values());
        model.addAttribute("combustiveis", TipoCombustivel.values());
        return "onibus-form";
    }

    // ── Salvar novo ônibus ───────
    @PostMapping("/onibus/novo")
    public String salvar(@ModelAttribute Onibus onibus,
                         @RequestParam Long terminalId,
                         RedirectAttributes ra) {
        Terminal terminal = terminalRepository.findById(terminalId)
                .orElseThrow(() -> new IllegalArgumentException("Terminal não encontrado"));
        onibus.setTerminal(terminal);
        onibusRepository.save(onibus);
        ra.addFlashAttribute("sucesso", "Ônibus cadastrado com sucesso!");
        return "redirect:/onibus";
    }

    // ── Formulário editar ──────
    @GetMapping("/onibus/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Onibus onibus = onibusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ônibus não encontrado: " + id));
        model.addAttribute("onibus",       onibus);
        model.addAttribute("terminais",    terminalRepository.findAll());
        model.addAttribute("tipos",        TipoOnibus.values());
        model.addAttribute("padroes",      PadraoMotor.values());
        model.addAttribute("combustiveis", TipoCombustivel.values());
        return "onibus-form";
    }

    // ── Salvar edição ─────
    @PostMapping("/onibus/editar/{id}")
    public String salvarEdicao(@PathVariable Long id,
                               @ModelAttribute Onibus dados,
                               @RequestParam Long terminalId,
                               RedirectAttributes ra) {
        Onibus onibus = onibusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ônibus não encontrado: " + id));
        Terminal terminal = terminalRepository.findById(terminalId)
                .orElseThrow(() -> new IllegalArgumentException("Terminal não encontrado"));

        onibus.setPrefixo(dados.getPrefixo());
        onibus.setTipo(dados.getTipo());
        onibus.setPadraoMotor(dados.getPadraoMotor());
        onibus.setCombustivel(dados.getCombustivel());
        onibus.setTemArCondicionado(dados.isTemArCondicionado());
        onibus.setKmAnuais(dados.getKmAnuais());
        onibus.setAnoFabricacao(dados.getAnoFabricacao());
        onibus.setTerminal(terminal);
        onibusRepository.save(onibus);

        ra.addFlashAttribute("sucesso", "Ônibus atualizado com sucesso!");
        return "redirect:/onibus";
    }

    // ── Excluir ────
    @PostMapping("/onibus/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        onibusRepository.deleteById(id);
        ra.addFlashAttribute("sucesso", "Ônibus removido.");
        return "redirect:/onibus";
    }
}