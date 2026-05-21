package com.ecoterminal.service;

import com.ecoterminal.model.EmissaoCO2;
import org.springframework.stereotype.Service;

@Service
public class ConformidadeService {

    // ─── Linha de base 2016 — SPTrans ──────
    public static final double CO2_BASE_2016_TON = 1_240_000.0;
    public static final double MP_BASE_2016_TON  =       144.7;
    public static final double NOX_BASE_2016_TON =     9_130.0;

    /**
     * Total VKT da frota de referência SP 2016 (km/ano).
     * Soma das colunas "Total (mi km/ano)" da tabela SPTrans:
     * 251+114+215+259+13+86+64+8+10 ≈ 1.020 mi km
     */
    private static final double VKT_BASE_FROTA_KM = 1_020_000_000.0;

    // ─── DTO de resultado ───────────
    public record ConformidadeReport(
            boolean conformeCo2,
            boolean conformeMp,
            boolean conformeNox,
            double  percentualCo2VsBase,
            double  percentualMpVsBase,
            double  percentualNoxVsBase,
            String  descricaoMeta
    ) {}

    // ─── API pública ─────

    /**
     * Verifica a conformidade de um resultado de emissão calculado.
     *
     * @param emissao resultado produzido por {@link EmissaoCalculoService}
     * @param ano     ano de referência — determina qual meta aplicar
     * @return relatório de conformidade (imutável)
     */
    public ConformidadeReport verificar(EmissaoCO2 emissao, int ano) {
        double vkt       = emissao.getVktKmAno();
        double co2Onibus = emissao.getCo2Toneladas();
        double mpOnibus  = emissao.getMpToneladas();
        double noxOnibus = emissao.getNoxToneladas();

        // Cota proporcional da linha de base para este ônibus (pelo VKT)
        double proporcao  = vkt / VKT_BASE_FROTA_KM;
        double limBaseCo2 = CO2_BASE_2016_TON * proporcao;
        double limBaseMp  = MP_BASE_2016_TON  * proporcao;
        double limBaseNox = NOX_BASE_2016_TON * proporcao;

        boolean meta20 = ano >= 2038;
        boolean meta10 = ano >= 2028;

        // Limites conforme horizonte temporal
        double limCo2 = meta20 ? 0.0
                      : meta10 ? limBaseCo2 * 0.50
                      :          limBaseCo2;

        double limMp  = meta20 ? limBaseMp * 0.05
                      : meta10 ? limBaseMp * 0.10
                      :          limBaseMp;

        double limNox = meta20 ? limBaseNox * 0.05
                      : meta10 ? limBaseNox * 0.20
                      :          limBaseNox;

        String descMeta = meta20
                ? "Meta 20 anos (2038): CO2 -100%, MP -95%, NOx -95%"
                : meta10
                ? "Meta 10 anos (2028): CO2 -50%, MP -90%, NOx -80%"
                : "Linha de base (2016) — sem redução exigida ainda";

        return new ConformidadeReport(
                co2Onibus <= limCo2,
                mpOnibus  <= limMp,
                noxOnibus <= limNox,
                limBaseCo2 > 0 ? co2Onibus  / limBaseCo2 : 0.0,
                limBaseMp  > 0 ? mpOnibus   / limBaseMp  : 0.0,
                limBaseNox > 0 ? noxOnibus  / limBaseNox : 0.0,
                descMeta
        );
    }

    /**
     * Atualiza os flags de conformidade diretamente na entidade {@link EmissaoCO2}.
     * Conveniente para persistir em conjunto com o cálculo.
     *
     * @param emissao entidade a atualizar (modificada in-place)
     * @param ano     ano de referência
     */
    public void aplicarConformidade(EmissaoCO2 emissao, int ano) {
        ConformidadeReport report = verificar(emissao, ano);
        emissao.setConformeCo2(report.conformeCo2());
        emissao.setConformeMp(report.conformeMp());
        emissao.setConformeNox(report.conformeNox());
        emissao.setReducaoCo2Percentual(
                (1.0 - report.percentualCo2VsBase()) * 100.0);
    }
}
