package com.ecoterminal.controller;

import com.ecoterminal.dto.LeituraDTO;
import com.ecoterminal.service.LeituraService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/leituras")
@CrossOrigin(origins = "*")
public class LeituraController {

    private final LeituraService leituraService;

    public LeituraController(LeituraService leituraService) {
        this.leituraService = leituraService;
    }
    @PostMapping
    public ResponseEntity<LeituraDTO> registrar(@Valid @RequestBody LeituraDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leituraService.registrar(dto));
    }
    @GetMapping("/onibus/{onibusId}")
    public ResponseEntity<List<LeituraDTO>> listarPorOnibus(@PathVariable Long onibusId) {
        return ResponseEntity.ok(leituraService.listarPorOnibus(onibusId));
    }
    @GetMapping("/terminal/{terminalId}/periodo")
    public ResponseEntity<List<LeituraDTO>> listarPorTerminalEPeriodo(
            @PathVariable Long terminalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(
                leituraService.listarPorTerminalEPeriodo(terminalId, inicio, fim));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        leituraService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}