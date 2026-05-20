package com.ecoterminal.controller;

import com.ecoterminal.model.Terminal;
import com.ecoterminal.service.TerminalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terminais")
@CrossOrigin(origins = "*")
public class TerminaisController {

    private final TerminalService terminalService;

    public TerminaisController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @GetMapping
    public ResponseEntity<List<Terminal>> listarTodos() {
        return ResponseEntity.ok(terminalService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Terminal> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(terminalService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Terminal> cadastrar(@Valid @RequestBody Terminal terminal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(terminalService.cadastrar(terminal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Terminal> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Terminal dados) {
        return ResponseEntity.ok(terminalService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        terminalService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}