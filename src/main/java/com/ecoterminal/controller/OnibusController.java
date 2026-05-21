package com.ecoterminal.controller;

import com.ecoterminal.dto.OnibusDTO;
import com.ecoterminal.model.EmissaoCO2;
import com.ecoterminal.service.EmissaoCalculoService;
import com.ecoterminal.service.OnibusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;
@RestController
@RequestMapping("/api/onibus")
@CrossOrigin(origins = "*")
public class OnibusController {

    private final OnibusService         onibusService;
    private final EmissaoCalculoService emissaoCalculoService;

    public OnibusController(OnibusService onibusService,
                            EmissaoCalculoService emissaoCalculoService) {
        this.onibusService         = onibusService;
        this.emissaoCalculoService = emissaoCalculoService;
    }

    // ── CRUD ────

    @GetMapping
    public ResponseEntity<List<OnibusDTO>> listarTodos() {
        return ResponseEntity.ok(onibusService.listarTodos());
    }
    @GetMapping("/{id}")
    public ResponseEntity<OnibusDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(onibusService.buscarPorId(id));
    }
    @GetMapping("/prefixo/{prefixo}")
    public ResponseEntity<OnibusDTO> buscarPorPrefixo(@PathVariable String prefixo) {
        return ResponseEntity.ok(onibusService.buscarPorPrefixo(prefixo));
    }
    @GetMapping("/terminal/{terminalId}")
    public ResponseEntity<List<OnibusDTO>> listarPorTerminal(@PathVariable Long terminalId) {
        return ResponseEntity.ok(onibusService.listarPorTerminal(terminalId));
    }
    @PostMapping
    public ResponseEntity<OnibusDTO> cadastrar(@Valid @RequestBody OnibusDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(onibusService.cadastrar(dto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<OnibusDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody OnibusDTO dto) {
        return ResponseEntity.ok(onibusService.atualizar(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        onibusService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ── Emissões ────
    @GetMapping("/{id}/emissao")
    public ResponseEntity<EmissaoCO2> calcularEmissao(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int ano) {
        int anoCalculo = resolverAno(ano);
        return ResponseEntity.ok(
                emissaoCalculoService.calcular(onibusService.buscarEntidade(id), anoCalculo));
    }
    @PostMapping("/{id}/emissao")
    public ResponseEntity<EmissaoCO2> calcularEPersistirEmissao(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int ano) {
        int anoCalculo = resolverAno(ano);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(emissaoCalculoService.calcularEPersistir(
                        onibusService.buscarEntidade(id), anoCalculo));
    }
 @GetMapping("/terminal/{terminalId}/emissao")
public ResponseEntity<List<EmissaoCO2>> calcularEmissaoTerminal(
        @PathVariable Long terminalId,
        @RequestParam(defaultValue = "0") int ano) {
    int anoCalculo = resolverAno(ano);

    List<EmissaoCO2> resultados = onibusService.buscarEntidadesPorTerminal(terminalId)
            .stream()
            .map(onibus -> emissaoCalculoService.calcular(onibus, anoCalculo))
            .toList();

    return ResponseEntity.ok(resultados);
}

    // ── Helper ────

    private static int resolverAno(int ano) {
        return (ano == 0) ? Year.now().getValue() : ano;
    }
}