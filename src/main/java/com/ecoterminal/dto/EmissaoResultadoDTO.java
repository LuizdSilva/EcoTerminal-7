package com.ecoterminal.dto;

import com.ecoterminal.model.EmissaoCO2;

public class EmissaoResultadoDTO {

    private Long emissaoId;
    private Long onibusId;
    private String onibusPrefixo;
    private int anoReferencia;
    private double co2Toneladas;
    private double mpToneladas;
    private double noxToneladas;
    private double vktKmAno;
    private double consumoEnergiaKwhKm;
    private double efCo2GKwh;
    private double efMpGKm;
    private double efNoxGKm;
    private Boolean conformeCo2;
    private Boolean conformeMp;
    private Boolean conformeNox;
    private Double reducaoCo2Percentual;

    public static EmissaoResultadoDTO fromEntity(EmissaoCO2 e) {
        EmissaoResultadoDTO dto = new EmissaoResultadoDTO();
        dto.emissaoId            = e.getId();
        dto.onibusId             = e.getOnibus().getId();
        dto.onibusPrefixo        = e.getOnibus().getPrefixo();
        dto.anoReferencia        = e.getAnoReferencia();
        dto.co2Toneladas         = e.getCo2Toneladas();
        dto.mpToneladas          = e.getMpToneladas();
        dto.noxToneladas         = e.getNoxToneladas();
        dto.vktKmAno             = e.getVktKmAno();
        dto.consumoEnergiaKwhKm  = e.getConsumoEnergiaKwhKm();
        dto.efCo2GKwh            = e.getEfCo2GKwh();
        dto.efMpGKm              = e.getEfMpGKm();
        dto.efNoxGKm             = e.getEfNoxGKm();
        dto.conformeCo2          = e.getConformeCo2();
        dto.conformeMp           = e.getConformeMp();
        dto.conformeNox          = e.getConformeNox();
        dto.reducaoCo2Percentual = e.getReducaoCo2Percentual();
        return dto;
    }

    public Long getEmissaoId()                                           { return emissaoId; }
    public void setEmissaoId(Long emissaoId)                             { this.emissaoId = emissaoId; }

    public Long getOnibusId()                                            { return onibusId; }
    public void setOnibusId(Long onibusId)                               { this.onibusId = onibusId; }

    public String getOnibusPrefixo()                                     { return onibusPrefixo; }
    public void setOnibusPrefixo(String onibusPrefixo)                   { this.onibusPrefixo = onibusPrefixo; }

    public int getAnoReferencia()                                        { return anoReferencia; }
    public void setAnoReferencia(int anoReferencia)                      { this.anoReferencia = anoReferencia; }

    public double getCo2Toneladas()                                      { return co2Toneladas; }
    public void setCo2Toneladas(double co2Toneladas)                     { this.co2Toneladas = co2Toneladas; }

    public double getMpToneladas()                                       { return mpToneladas; }
    public void setMpToneladas(double mpToneladas)                       { this.mpToneladas = mpToneladas; }

    public double getNoxToneladas()                                      { return noxToneladas; }
    public void setNoxToneladas(double noxToneladas)                     { this.noxToneladas = noxToneladas; }

    public double getVktKmAno()                                          { return vktKmAno; }
    public void setVktKmAno(double vktKmAno)                             { this.vktKmAno = vktKmAno; }

    public double getConsumoEnergiaKwhKm()                               { return consumoEnergiaKwhKm; }
    public void setConsumoEnergiaKwhKm(double consumoEnergiaKwhKm)       { this.consumoEnergiaKwhKm = consumoEnergiaKwhKm; }

    public double getEfCo2GKwh()                                         { return efCo2GKwh; }
    public void setEfCo2GKwh(double efCo2GKwh)                           { this.efCo2GKwh = efCo2GKwh; }

    public double getEfMpGKm()                                           { return efMpGKm; }
    public void setEfMpGKm(double efMpGKm)                               { this.efMpGKm = efMpGKm; }

    public double getEfNoxGKm()                                          { return efNoxGKm; }
    public void setEfNoxGKm(double efNoxGKm)                             { this.efNoxGKm = efNoxGKm; }

    public Boolean getConformeCo2()                                      { return conformeCo2; }
    public void setConformeCo2(Boolean conformeCo2)                      { this.conformeCo2 = conformeCo2; }

    public Boolean getConformeMp()                                       { return conformeMp; }
    public void setConformeMp(Boolean conformeMp)                        { this.conformeMp = conformeMp; }

    public Boolean getConformeNox()                                      { return conformeNox; }
    public void setConformeNox(Boolean conformeNox)                      { this.conformeNox = conformeNox; }

    public Double getReducaoCo2Percentual()                              { return reducaoCo2Percentual; }
    public void setReducaoCo2Percentual(Double reducaoCo2Percentual)     { this.reducaoCo2Percentual = reducaoCo2Percentual; }
}
