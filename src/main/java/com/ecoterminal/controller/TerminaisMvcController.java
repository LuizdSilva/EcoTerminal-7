package com.ecoterminal.controller;

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
@Controller
@RequiredArgsConstructor
public class TerminaisMvcController {

    private final TerminalRepository terminalRepository;
    private final OnibusRepository onibusRepository;
    private final LeituraCO2Repository leituraRepository;

    // ── Lista de terminais ───
    @GetMapping("/terminais")
    public String listar(Model model) {
        model.addAttribute("terminais", terminalRepository.findAll());
        return "terminais";
    }
    @GetMapping("/terminais/{id}/emissoes")
    public String emissoes(@PathVariable Long id, Model model) {
        Terminal terminal = terminalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Terminal não encontrado: " + id));

        LocalDateTime inicio = LocalDateTime.now().minusHours(24);
        var leituras = leituraRepository.findByTerminalIdAndPeriodo(
                id, inicio, LocalDateTime.now());

        model.addAttribute("terminal", terminal);
        model.addAttribute("leituras", leituras);
        model.addAttribute("onibus",   onibusRepository.findByTerminalId(id));
        return "leituras";
    }

    // ── Formulário novo terminal ────
    @GetMapping("/terminais/novo")
    public String novoForm(Model model) {
    model.addAttribute("terminal", new Terminal());
    return "terminais-form";  
    }
    // ── Salvar novo terminal ───
    @PostMapping("/terminais/novo")
    public String salvar(@ModelAttribute Terminal terminal, RedirectAttributes ra) {
        terminalRepository.save(terminal);
        ra.addFlashAttribute("sucesso", "Terminal cadastrado com sucesso!");
        return "redirect:/terminais";
    }

    // ── Formulário editar terminal ─────
    @GetMapping("/terminais/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Terminal terminal = terminalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Terminal não encontrado: " + id));
        model.addAttribute("terminal", terminal);
        return "terminal-form";
    }

    // ── Salvar edição ───
    @PostMapping("/terminais/editar/{id}")
    public String salvarEdicao(@PathVariable Long id,
                               @ModelAttribute Terminal dados,
                               RedirectAttributes ra) {
        Terminal terminal = terminalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Terminal não encontrado: " + id));
        terminal.setNome(dados.getNome());
        terminal.setCodigo(dados.getCodigo());
        terminal.setCidade(dados.getCidade());
        terminal.setEstado(dados.getEstado());
        terminalRepository.save(terminal);
        ra.addFlashAttribute("sucesso", "Terminal atualizado com sucesso!");
        return "redirect:/terminais";
    }

    // ── Excluir terminal ────
    @PostMapping("/terminais/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        terminalRepository.deleteById(id);
        ra.addFlashAttribute("sucesso", "Terminal removido.");
        return "redirect:/terminais";
    }
}