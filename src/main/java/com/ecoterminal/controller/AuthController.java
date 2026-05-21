package com.ecoterminal.controller;

import com.ecoterminal.dto.CadastroDTO;
import com.ecoterminal.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    // ── Página de login ────────
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String erro,
            @RequestParam(required = false) String logout,
            Model model) {
        if (erro   != null) model.addAttribute("erro",   "E-mail ou senha inválidos.");
        if (logout != null) model.addAttribute("logout", "Você saiu com sucesso.");
        return "login";
    }

    // ── Página de cadastro ─────────
    @GetMapping("/cadastro")
    public String cadastroPage(Model model) {
        model.addAttribute("cadastroDTO", new CadastroDTO("", "", "", ""));
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @Valid @ModelAttribute("cadastroDTO") CadastroDTO dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

                if (bindingResult.hasErrors()) {
            return "cadastro";
        }
        if (!dto.senhasConferem()) {
            model.addAttribute("erroCadastro", "As senhas não conferem.");
            return "cadastro";
        }
        try {
            usuarioService.cadastrar(dto);
            redirectAttributes.addFlashAttribute("sucesso",
                    "Cadastro realizado! Faça login para continuar.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erroCadastro", ex.getMessage());
            return "cadastro";
        }
    }
}