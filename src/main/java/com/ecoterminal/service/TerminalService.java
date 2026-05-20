package com.ecoterminal.service;

import com.ecoterminal.model.Terminal;
import com.ecoterminal.repository.TerminalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class TerminalService {

    private final TerminalRepository terminalRepository;

    public TerminalService(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

    // ─── Leitura ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Terminal> listarTodos() {
        return terminalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Terminal buscarPorId(Long id) {
        return terminalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Terminal não encontrado: " + id));
    }

    // ─── Escrita ──────────────────────────────────────────────────────────────

    public Terminal cadastrar(Terminal terminal) {
        if (terminalRepository.existsByCodigo(terminal.getCodigo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Código de terminal já existe: " + terminal.getCodigo());
        }
        return terminalRepository.save(terminal);
    }

    public Terminal atualizar(Long id, Terminal dados) {
        Terminal terminal = buscarPorId(id);
        terminal.setNome(dados.getNome());
        terminal.setCidade(dados.getCidade());
        terminal.setEstado(dados.getEstado());
        return terminalRepository.save(terminal);
    }

    public void deletar(Long id) {
        if (!terminalRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Terminal não encontrado: " + id);
        }
        terminalRepository.deleteById(id);
    }
}
