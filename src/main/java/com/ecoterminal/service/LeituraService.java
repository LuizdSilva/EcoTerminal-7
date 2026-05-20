package com.ecoterminal.service;

import com.ecoterminal.dto.LeituraDTO;
import com.ecoterminal.model.EmissaoCO2;
import com.ecoterminal.model.LeituraCO2;
import com.ecoterminal.model.Onibus;
import com.ecoterminal.repository.EmissaoCO2Repository;
import com.ecoterminal.repository.LeituraCO2Repository;
import com.ecoterminal.repository.OnibusRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LeituraService {

    private final LeituraCO2Repository  leituraRepository;
    private final EmissaoCO2Repository  emissaoCO2Repository;
    private final OnibusRepository      onibusRepository;
    private final OnibusService         onibusService;
    private final EmissaoCalculoService calculoService;
    private final ConformidadeService   conformidadeService;
    private final AlertaService         alertaService;

    public LeituraService(LeituraCO2Repository  leituraRepository,
                          EmissaoCO2Repository  emissaoCO2Repository,
                          OnibusRepository      onibusRepository,
                          OnibusService         onibusService,
                          EmissaoCalculoService calculoService,
                          ConformidadeService   conformidadeService,
                          AlertaService         alertaService) {
        this.leituraRepository    = leituraRepository;
        this.emissaoCO2Repository = emissaoCO2Repository;
        this.onibusRepository     = onibusRepository;
        this.onibusService        = onibusService;
        this.calculoService       = calculoService;
        this.conformidadeService  = conformidadeService;
        this.alertaService        = alertaService;
    }

    // ─── Leitura ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<LeituraDTO> listarPorOnibus(Long onibusId) {
        return leituraRepository.findByOnibusId(onibusId)
                .stream()
                .map(LeituraDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeituraDTO> listarPorTerminalEPeriodo(Long terminalId,
                                                       LocalDateTime inicio,
                                                       LocalDateTime fim) {
        return leituraRepository.findByTerminalIdAndPeriodo(terminalId, inicio, fim)
                .stream()
                .map(LeituraDTO::fromEntity)
                .toList();
    }

    // ─── Registro ────────────────────────────────────────────────────────────

    /**
     * Registra uma nova leitura e automaticamente:
     * <ol>
     *   <li>Recalcula emissões anuais do ônibus com VKT real acumulado</li>
     *   <li>Aplica conformidade Lei 16.802</li>
     *   <li>Gera alertas se houver ultrapassagem de limites</li>
     * </ol>
     *
     * @param dto dados da leitura
     * @return DTO da leitura persistida
     */
    public LeituraDTO registrar(LeituraDTO dto) {
        Onibus onibus = onibusService.buscarEntidade(dto.getOnibusId());

        LeituraCO2 leitura = new LeituraCO2(onibus, dto.getDataHora(), dto.getKmPercorridos());
        leitura.setCombustivelLitros(dto.getCombustivelLitros());
        leitura.setNoxPpm(dto.getNoxPpm());
        leitura.setMpMgM3(dto.getMpMgM3());
        leitura.setTemperaturaArAdmissao(dto.getTemperaturaArAdmissao());
        leitura.setUmidadeAbsolutaAr(dto.getUmidadeAbsolutaAr());
        leitura.setObservacoes(dto.getObservacoes());

        leituraRepository.save(leitura);

        // Recalcular e persistir emissões anuais após nova leitura
        recalcularEmissaoAnual(onibus);

        return LeituraDTO.fromEntity(leitura);
    }

    public void deletar(Long id) {
        LeituraCO2 leitura = leituraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leitura não encontrada: " + id));
        leituraRepository.delete(leitura);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Recalcula as emissões anuais do ônibus usando o VKT real acumulado no ano corrente.
     * Se houver dado real de quilometragem, atualiza e persiste a entidade Onibus
     * antes de calcular as emissões, garantindo consistência no banco.
     */
    private void recalcularEmissaoAnual(Onibus onibus) {
        int anoAtual = LocalDateTime.now().getYear();

        // Soma VKT real acumulado no ano a partir das leituras
        Double vktReal = leituraRepository
                .sumKmPercorridosByOnibusIdAndAno(onibus.getId(), anoAtual);

        if (vktReal != null && vktReal > 0) {
            onibus.setKmAnuais(vktReal);
            onibusRepository.save(onibus); // persiste a atualização do VKT
        }

        EmissaoCO2 emissao = calculoService.calcular(onibus, anoAtual);
        conformidadeService.aplicarConformidade(emissao, anoAtual);

        // Atualiza o registro existente ou insere um novo para o ano corrente
        emissaoCO2Repository.findByOnibusIdAndAnoReferencia(onibus.getId(), anoAtual)
                .ifPresentOrElse(
                        existente -> {
                            existente.setCo2Toneladas(emissao.getCo2Toneladas());
                            existente.setMpToneladas(emissao.getMpToneladas());
                            existente.setNoxToneladas(emissao.getNoxToneladas());
                            existente.setVktKmAno(emissao.getVktKmAno());
                            existente.setConformeCo2(emissao.getConformeCo2());
                            existente.setConformeMp(emissao.getConformeMp());
                            existente.setConformeNox(emissao.getConformeNox());
                            existente.setReducaoCo2Percentual(emissao.getReducaoCo2Percentual());
                            emissaoCO2Repository.save(existente);
                            alertaService.verificarEGerarAlertas(existente);
                        },
                        () -> {
                            emissao.setOnibus(onibus);
                            EmissaoCO2 salvo = emissaoCO2Repository.save(emissao);
                            alertaService.verificarEGerarAlertas(salvo);
                        }
                );
    }
}
