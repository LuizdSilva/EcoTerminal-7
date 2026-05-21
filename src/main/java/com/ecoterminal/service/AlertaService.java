package com.ecoterminal.service;

import com.ecoterminal.model.AlertaEmissao;
import com.ecoterminal.model.AlertaEmissao.Severidade;
import com.ecoterminal.model.AlertaEmissao.TipoAlerta;
import com.ecoterminal.model.EmissaoCO2;
import com.ecoterminal.model.Onibus;
import com.ecoterminal.repository.AlertaEmissaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AlertaService {

    private final AlertaEmissaoRepository alertaRepository;

    public AlertaService(AlertaEmissaoRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    // ─── Leitura ────

    @Transactional(readOnly = true)
    public List<AlertaEmissao> listarPorTerminal(Long terminalId) {
        return alertaRepository.findByOnibusTerminalIdAndReconhecidoFalse(terminalId);
    }

    @Transactional(readOnly = true)
    public List<AlertaEmissao> listarPorOnibus(Long onibusId) {
        return alertaRepository.findByOnibusId(onibusId);
    }

    @Transactional(readOnly = true)
    public long contarNaoReconhecidos(Long terminalId) {
        return alertaRepository.countByOnibusTerminalIdAndReconhecidoFalse(terminalId);
    }

    // ─── Ações ─────

    public AlertaEmissao reconhecer(Long alertaId) {
        AlertaEmissao alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Alerta não encontrado: " + alertaId));
        alerta.setReconhecido(true);
        return alertaRepository.save(alerta);
    }

    // ─── Geração automática ──────
    /**
     * Chamado automaticamente pelo {@link LeituraService} após cada recálculo.
     * Verifica os EF usados vs limites P7 e a conformidade com a Lei 16.802.
     *
     * @param emissao resultado de emissão já persistido e com conformidade aplicada
     */
    public void verificarEGerarAlertas(EmissaoCO2 emissao) {
        Onibus onibus = emissao.getOnibus();
        LocalDateTime agora = LocalDateTime.now();

        double limNoxP7 = 4.0;
        double efNox    = emissao.getEfNoxGKm();
        if (efNox > limNoxP7) {
            Severidade sev = efNox > limNoxP7 * 2 ? Severidade.CRITICO : Severidade.AVISO;
            criarSeNaoExistir(new AlertaEmissao(
                    onibus, TipoAlerta.NOX_ACIMA_LIMITE, sev, agora,
                    String.format("NOx = %.3f g/km — limite P7: %.1f g/km (padrão %s)",
                            efNox, limNoxP7, onibus.getPadraoMotor()),
                    efNox, limNoxP7
            ));
        }

        double limMpP7 = 0.04;
        double efMp    = emissao.getEfMpGKm();
        if (efMp > limMpP7) {
            Severidade sev = efMp > limMpP7 * 2 ? Severidade.CRITICO : Severidade.AVISO;
            criarSeNaoExistir(new AlertaEmissao(
                    onibus, TipoAlerta.MP_ACIMA_LIMITE, sev, agora,
                    String.format("MP = %.4f g/km — limite P7: %.3f g/km (padrão %s)",
                            efMp, limMpP7, onibus.getPadraoMotor()),
                    efMp, limMpP7
            ));
        }

        if (Boolean.FALSE.equals(emissao.getConformeCo2())) {
            criarSeNaoExistir(new AlertaEmissao(
                    onibus, TipoAlerta.CO2_META_NAO_ATINGIDA, Severidade.AVISO, agora,
                    String.format("CO2 = %.2f t/ano — meta Lei 16.802 não atingida para %d",
                            emissao.getCo2Toneladas(), emissao.getAnoReferencia()),
                    emissao.getCo2Toneladas(), null
            ));
        }

        // ── Conformidade geral Lei 16.802 (MP ou NOx fora)
        boolean fora = Boolean.FALSE.equals(emissao.getConformeMp())
                    || Boolean.FALSE.equals(emissao.getConformeNox());
        if (fora) {
            criarSeNaoExistir(new AlertaEmissao(
                    onibus, TipoAlerta.CONFORMIDADE_LEI_16802, Severidade.CRITICO, agora,
                    String.format("Ônibus %s fora de conformidade com a Lei 16.802/%d — MP:%s NOx:%s",
                            onibus.getPrefixo(), emissao.getAnoReferencia(),
                            Boolean.FALSE.equals(emissao.getConformeMp()) ? "✗" : "✓",
                            Boolean.FALSE.equals(emissao.getConformeNox()) ? "✗" : "✓"),
                    null, null
            ));
        }
    }

    // ─── Helper ───────
    private void criarSeNaoExistir(AlertaEmissao novo) {
        boolean jaExiste = alertaRepository.existsAtivoByOnibusIdAndTipo(
                novo.getOnibus().getId(), novo.getTipo());
        if (!jaExiste) {
            alertaRepository.save(novo);
        }
    }
}
