package com.ecoterminal.service;

import com.ecoterminal.enums.PadraoMotor;
import com.ecoterminal.enums.TipoCombustivel;
import com.ecoterminal.enums.TipoOnibus;
import com.ecoterminal.model.EmissaoCO2;
import com.ecoterminal.model.Onibus;
import com.ecoterminal.repository.EmissaoCO2Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

@Service
public class EmissaoCalculoService {

    // CONSTANTES — CONSUMO ENERGÉTICO (kWh/km) — fonte: SPTrans 2017   

    private static final Map<TipoOnibus, Double> EC_SEM_AC = new EnumMap<>(TipoOnibus.class);
    private static final Map<TipoOnibus, Double> AC_DELTA  = new EnumMap<>(TipoOnibus.class);

    static {
        EC_SEM_AC.put(TipoOnibus.MINIBUS,        3.0);
        EC_SEM_AC.put(TipoOnibus.MIDIBUS,        4.0);
        EC_SEM_AC.put(TipoOnibus.BASICO,         4.6);
        EC_SEM_AC.put(TipoOnibus.PADRON,         5.5);
        EC_SEM_AC.put(TipoOnibus.PADRON_15M,     6.5);
        EC_SEM_AC.put(TipoOnibus.ARTICULADO,     7.1);
        EC_SEM_AC.put(TipoOnibus.ARTICULADO_23M, 7.5);
        EC_SEM_AC.put(TipoOnibus.BIARTICULADO,   8.0);
        EC_SEM_AC.put(TipoOnibus.TROLEBUS,       1.8);  // elétrico — Dallmann et al.

        AC_DELTA.put(TipoOnibus.MINIBUS,        0.5);
        AC_DELTA.put(TipoOnibus.MIDIBUS,        0.7);
        AC_DELTA.put(TipoOnibus.BASICO,         0.7);
        AC_DELTA.put(TipoOnibus.PADRON,         0.8);
        AC_DELTA.put(TipoOnibus.PADRON_15M,     1.0);
        AC_DELTA.put(TipoOnibus.ARTICULADO,     0.9);
        AC_DELTA.put(TipoOnibus.ARTICULADO_23M, 1.0);
        AC_DELTA.put(TipoOnibus.BIARTICULADO,   1.0);
        AC_DELTA.put(TipoOnibus.TROLEBUS,       0.0);
    }

    // CONSTANTES — EF_NOx (g/km) — fonte: MMA 2011 / HBEFA 3.3

    private static final Map<TipoOnibus, Map<PadraoMotor, Double>> EF_NOX = new EnumMap<>(TipoOnibus.class);

    static {
        EF_NOX.put(TipoOnibus.MINIBUS,        efNoxMap(5.1,  2.1,  0.42));
        EF_NOX.put(TipoOnibus.MIDIBUS,        efNoxMap(6.1,  2.5,  0.42));
        EF_NOX.put(TipoOnibus.BASICO,         efNoxMap(7.1,  2.9,  0.47));
        EF_NOX.put(TipoOnibus.PADRON,         efNoxMap(9.9,  4.0,  0.47));
        EF_NOX.put(TipoOnibus.PADRON_15M,     efNoxMap(9.9,  4.0,  0.47));
        EF_NOX.put(TipoOnibus.ARTICULADO,     efNoxMap(12.7, 5.1,  0.38));
        EF_NOX.put(TipoOnibus.ARTICULADO_23M, efNoxMap(13.7, 5.5,  0.38));
        EF_NOX.put(TipoOnibus.BIARTICULADO,   efNoxMap(16.2, 6.5,  0.38));
        EF_NOX.put(TipoOnibus.TROLEBUS,       efNoxMap(0.0,  0.0,  0.0));
    }

    // CONSTANTES — EF_MP (g/km) — fonte: MMA 2011 / HBEFA 3.3

    private static final Map<TipoOnibus, Map<PadraoMotor, Double>> EF_MP = new EnumMap<>(TipoOnibus.class);

    static {
        EF_MP.put(TipoOnibus.MINIBUS,        efMpMap(0.085, 0.021, 0.005));
        EF_MP.put(TipoOnibus.MIDIBUS,        efMpMap(0.103, 0.025, 0.005));
        EF_MP.put(TipoOnibus.BASICO,         efMpMap(0.119, 0.029, 0.006));
        EF_MP.put(TipoOnibus.PADRON,         efMpMap(0.165, 0.040, 0.006));
        EF_MP.put(TipoOnibus.PADRON_15M,     efMpMap(0.165, 0.040, 0.006));
        EF_MP.put(TipoOnibus.ARTICULADO,     efMpMap(0.213, 0.052, 0.007));
        EF_MP.put(TipoOnibus.ARTICULADO_23M, efMpMap(0.229, 0.056, 0.007));
        EF_MP.put(TipoOnibus.BIARTICULADO,   efMpMap(0.271, 0.066, 0.007));
        EF_MP.put(TipoOnibus.TROLEBUS,       efMpMap(0.0,   0.0,   0.0));
    }

    // ─── Dependência ─────
    private final EmissaoCO2Repository emissaoCO2Repository;

    public EmissaoCalculoService(EmissaoCO2Repository emissaoCO2Repository) {
        this.emissaoCO2Repository = emissaoCO2Repository;
    }

    /**
     * Calcula e persiste as emissões anuais de um ônibus para o ano informado.
     *
     * @param onibus ônibus com tipo, padraoMotor, combustivel e kmAnuais preenchidos
     * @param ano    ano de referência do cálculo
     * @return entidade {@link EmissaoCO2} salva
     */
    @Transactional
    public EmissaoCO2 calcularEPersistir(Onibus onibus, int ano) {
        EmissaoCO2 resultado = calcular(onibus, ano);
        resultado.setOnibus(onibus);
        return emissaoCO2Repository.save(resultado);
    }

    /**
     * Calcula (sem persistir) as emissões anuais de um ônibus.
     * Útil para simulações e previews no dashboard.
     *
     * @param onibus ônibus a calcular
     * @param ano    ano de referência
     * @return entidade {@link EmissaoCO2} não gerenciada (transiente)
     */
    public EmissaoCO2 calcular(Onibus onibus, int ano) {
        double ec    = getConsumoEnergia(onibus);         
        double vkt   = onibus.getKmAnuais();              
        double efCo2 = getEfCo2(onibus.getCombustivel()); 
        double efMp  = getEfMp(onibus);                   
        double efNox = getEfNox(onibus);                  

        // Equações ICCT 2019 — Seção 3.8
        double co2Ton = ec * efCo2 * vkt * 1e-9;  
        double mpTon  = efMp * vkt * 1e-6;        
        double noxTon = efNox * vkt * 1e-6;        

        return new EmissaoCO2(
                onibus, ano, LocalDate.now(),
                co2Ton, mpTon, noxTon,
                vkt, ec, efCo2, efMp, efNox
        );
    }
    public double getConsumoEnergia(Onibus onibus) {
        double base  = EC_SEM_AC.getOrDefault(onibus.getTipo(), 5.5);
        double delta = onibus.isTemArCondicionado()
                ? AC_DELTA.getOrDefault(onibus.getTipo(), 0.0)
                : 0.0;
        return base + delta;
    }

    /** Fator de emissão CO2 (g/kWh) conforme o combustível operado. */
    public double getEfCo2(TipoCombustivel combustivel) {
        return combustivel.getEfCo2GKwh();
    }

    /** Fator de emissão MP (g/km) conforme tipo e padrão de motor. */
    public double getEfMp(Onibus onibus) {
        return EF_MP
                .getOrDefault(onibus.getTipo(), Map.of())
                .getOrDefault(onibus.getPadraoMotor(), 0.0);
    }

    /** Fator de emissão NOx (g/km) conforme tipo e padrão de motor. */
    public double getEfNox(Onibus onibus) {
        return EF_NOX
                .getOrDefault(onibus.getTipo(), Map.of())
                .getOrDefault(onibus.getPadraoMotor(), 0.0);
    }

    // HELPERS DE INICIALIZAÇÃO DOS MAPAS
    private static Map<PadraoMotor, Double> efNoxMap(double p5, double p7, double p8) {
        Map<PadraoMotor, Double> m = new EnumMap<>(PadraoMotor.class);
        m.put(PadraoMotor.P5_EURO_III, p5);
        m.put(PadraoMotor.P7_EURO_V,   p7);
        m.put(PadraoMotor.P8_EURO_VI,  p8);
        m.put(PadraoMotor.ELETRICO,    0.0);
        return m;
    }
    private static Map<PadraoMotor, Double> efMpMap(double p5, double p7, double p8) {
        Map<PadraoMotor, Double> m = new EnumMap<>(PadraoMotor.class);
        m.put(PadraoMotor.P5_EURO_III, p5);
        m.put(PadraoMotor.P7_EURO_V,   p7);
        m.put(PadraoMotor.P8_EURO_VI,  p8);
        m.put(PadraoMotor.ELETRICO,    0.0);
        return m;
    }
}
