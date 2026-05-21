package com.ecoterminal.service;

import com.ecoterminal.dto.OnibusDTO;
import com.ecoterminal.model.Onibus;
import com.ecoterminal.model.Terminal;
import com.ecoterminal.repository.OnibusRepository;
import com.ecoterminal.repository.TerminalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Service
@Transactional
public class OnibusService {

    private final OnibusRepository    onibusRepository;
    private final TerminalRepository  terminalRepository;

    public OnibusService(OnibusRepository onibusRepository,
                         TerminalRepository terminalRepository) {
        this.onibusRepository  = onibusRepository;
        this.terminalRepository = terminalRepository;
    }

    // ─── Leitura ─────

    @Transactional(readOnly = true)
    public List<OnibusDTO> listarTodos() {
        return onibusRepository.findAll()
                .stream()
                .map(OnibusDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OnibusDTO> listarPorTerminal(Long terminalId) {
        return onibusRepository.findByTerminalId(terminalId)
                .stream()
                .map(OnibusDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public OnibusDTO buscarPorId(Long id) {
        return OnibusDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public OnibusDTO buscarPorPrefixo(String prefixo) {
        return onibusRepository.findByPrefixo(prefixo)
                .map(OnibusDTO::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ônibus não encontrado: " + prefixo));
    }

    // ─── Escrita ─────

    public OnibusDTO cadastrar(OnibusDTO dto) {
        validarPrefixoUnico(dto.getPrefixo(), null);

        Terminal terminal = buscarTerminal(dto.getTerminalId());

        Onibus onibus = new Onibus(
                dto.getPrefixo(),
                dto.getTipo(),
                dto.getPadraoMotor(),
                dto.getCombustivel(),
                dto.isTemArCondicionado(),
                dto.getKmAnuais(),
                dto.getAnoFabricacao(),
                terminal
        );

        return OnibusDTO.fromEntity(onibusRepository.save(onibus));
    }

    public OnibusDTO atualizar(Long id, OnibusDTO dto) {
        Onibus onibus = buscarEntidade(id);

        if (!onibus.getPrefixo().equals(dto.getPrefixo())) {
            validarPrefixoUnico(dto.getPrefixo(), id);
        }

        Terminal terminal = buscarTerminal(dto.getTerminalId());

        onibus.setPrefixo(dto.getPrefixo());
        onibus.setTipo(dto.getTipo());
        onibus.setPadraoMotor(dto.getPadraoMotor());
        onibus.setCombustivel(dto.getCombustivel());
        onibus.setTemArCondicionado(dto.isTemArCondicionado());
        onibus.setKmAnuais(dto.getKmAnuais());
        onibus.setAnoFabricacao(dto.getAnoFabricacao());
        onibus.setTerminal(terminal);

        return OnibusDTO.fromEntity(onibusRepository.save(onibus));
    }

    public void deletar(Long id) {
        if (!onibusRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ônibus não encontrado: " + id);
        }
        onibusRepository.deleteById(id);
    }

    // ─── Helpers (package-visible para uso interno entre services) ───────
    @Transactional(readOnly = true)
    public Onibus buscarEntidade(Long id) {
        return onibusRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ônibus não encontrado: " + id));
    }
    @Transactional(readOnly = true)
    public List<Onibus> buscarEntidadesPorTerminal(Long terminalId) {
        return onibusRepository.findByTerminalId(terminalId);
}
    // ─── Privados ─────

    private Terminal buscarTerminal(Long terminalId) {
        return terminalRepository.findById(terminalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Terminal não encontrado: " + terminalId));
    }

    private void validarPrefixoUnico(String prefixo, Long idIgnorar) {
        onibusRepository.findByPrefixo(prefixo).ifPresent(existente -> {
            if (idIgnorar == null || !existente.getId().equals(idIgnorar)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Prefixo já cadastrado: " + prefixo);
            }
        });
    }
}
