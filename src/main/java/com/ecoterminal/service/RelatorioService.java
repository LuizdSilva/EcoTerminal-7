package com.ecoterminal.service;

import com.ecoterminal.dto.EmissaoResultadoDTO;
import com.ecoterminal.model.EmissaoCO2;
import com.ecoterminal.model.Onibus;
import com.ecoterminal.repository.EmissaoCO2Repository;
import com.ecoterminal.repository.OnibusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RelatorioService {

    private final EmissaoCO2Repository  emissaoCO2Repository;
    private final OnibusRepository      onibusRepository;
    private final EmissaoCalculoService calculoService;
    private final ConformidadeService   conformidadeService;

    public RelatorioService(EmissaoCO2Repository emissaoCO2Repository,
                            OnibusRepository onibusRepository,
                            EmissaoCalculoService calculoService,
                            ConformidadeService conformidadeService) {
        this.emissaoCO2Repository = emissaoCO2Repository;
        this.onibusRepository     = onibusRepository;
        this.calculoService       = calculoService;
        this.conformidadeService  = conformidadeService;
    }

    // ─── Dashboard ───

    /**
     * Resumo do terminal para o dashboard principal.
     *
     * @param terminalId ID do termina
     * @param ano        ano de referência
     * @return {@link DashboardResumo} com totais e percentual de redução
     */
    public DashboardResumo resumoTerminal(Long terminalId, int ano) {
        List<EmissaoCO2> emissoes = emissaoCO2Repository.findByTerminalIdAndAno(terminalId, ano);

        double totalCo2  = emissoes.stream().mapToDouble(EmissaoCO2::getCo2Toneladas).sum();
        double totalMp   = emissoes.stream().mapToDouble(EmissaoCO2::getMpToneladas).sum();
        double totalNox  = emissoes.stream().mapToDouble(EmissaoCO2::getNoxToneladas).sum();
        long totalOnibus = onibusRepository.countByTerminalId(terminalId);

        long naoConformes = emissoes.stream()
                .filter(e -> Boolean.FALSE.equals(e.getConformeCo2())
                          || Boolean.FALSE.equals(e.getConformeMp())
                          || Boolean.FALSE.equals(e.getConformeNox()))
                .count();

        // Percentual de redução CO2 vs linha de base 2016 proporcional ao VKT do terminal
        double vktTerminal = emissoes.stream().mapToDouble(EmissaoCO2::getVktKmAno).sum();
        double proporcao   = vktTerminal / ConformidadeService.CO2_BASE_2016_TON;
        double reducaoPct  = proporcao > 0
                ? (1.0 - totalCo2 / (ConformidadeService.CO2_BASE_2016_TON * proporcao)) * 100.0
                : 0.0;

        return new DashboardResumo(
                terminalId, ano,
                totalCo2, totalMp, totalNox,
                (int) totalOnibus, (int) naoConformes,
                reducaoPct
        );
    }

    /**
     * Lista os N ônibus com maior emissão de CO2 em um terminal/ano.
     *
     * @param terminalId ID do terminal
     * @param ano        ano de referência
     * @param top        quantidade máxima de registros a retornar
     * @return lista ordenada de forma decrescente por CO2
     */
    public List<EmissaoResultadoDTO> maioresEmissores(Long terminalId, int ano, int top) {
        return emissaoCO2Repository.findByTerminalIdAndAno(terminalId, ano)
                .stream()
                .sorted((a, b) -> Double.compare(b.getCo2Toneladas(), a.getCo2Toneladas()))
                .limit(top)
                .map(EmissaoResultadoDTO::fromEntity)
                .toList();
    }

    // ─── Relatório anual ──────────────────────────────────────────────────────

    /**
     * Relatório anual de emissões por ônibus de um terminal (obrigação Lei 16.802).
     * Recalcula ônibus que ainda não têm resultado no ano solicitado.
     *
     * @param terminalId ID do terminal
     * @param ano        ano de referência
     * @return lista de DTOs com resultado de emissão de cada ônibus
     */
    @Transactional
    public List<EmissaoResultadoDTO> relatorioAnualTerminal(Long terminalId, int ano) {
        List<Onibus> frota = onibusRepository.findByTerminalId(terminalId);

        return frota.stream().map(onibus -> {
            EmissaoCO2 emissao = emissaoCO2Repository
                    .findByOnibusIdAndAnoReferencia(onibus.getId(), ano)
                    .orElseGet(() -> {
                        EmissaoCO2 calculado = calculoService.calcular(onibus, ano);
                        conformidadeService.aplicarConformidade(calculado, ano);
                        calculado.setOnibus(onibus);
                        return emissaoCO2Repository.save(calculado);
                    });
            return EmissaoResultadoDTO.fromEntity(emissao);
        }).toList();
    }

    /**
     * Emissões históricas de um ônibus (todos os anos disponíveis).
     *
     * @param onibusId ID do ônibus
     * @return lista de resultados em ordem natural do repositório
     */
    public List<EmissaoResultadoDTO> historicoOnibus(Long onibusId) {
        return emissaoCO2Repository.findByOnibusId(onibusId)
                .stream()
                .map(EmissaoResultadoDTO::fromEntity)
                .toList();
    }

    /**
     * Distribuição de emissões por padrão de motor em um terminal/ano.
     * Útil para o gráfico de pizza do dashboard.
     *
     * @param terminalId ID do terminal
     * @param ano        ano de referência
     * @return mapa {@code padraoMotor.name() → soma CO2 em toneladas}
     */
    public Map<String, Double> co2PorPadraoMotor(Long terminalId, int ano) {
        return emissaoCO2Repository.findByTerminalIdAndAno(terminalId, ano)
                .stream()
                .collect(Collectors.groupingBy(
                        e -> e.getOnibus().getPadraoMotor().name(),
                        Collectors.summingDouble(EmissaoCO2::getCo2Toneladas)
                ));
    }

    // ─── Record de resumo ────

    public record DashboardResumo(
            Long   terminalId,
            int    ano,
            double co2ToneladasTotal,
            double mpToneladasTotal,
            double noxToneladasTotal,
            int    totalOnibus,
            int    onibusNaoConformes,
            double reducaoCo2Percentual
    ) {}
}
