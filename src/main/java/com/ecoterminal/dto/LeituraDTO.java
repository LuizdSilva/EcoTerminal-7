package com.ecoterminal.dto;

import com.ecoterminal.model.LeituraCO2;
import java.time.LocalDateTime;

public class LeituraDTO {

    private Long id;
    private Long onibusId;
    private String onibusPrefixo;
    private LocalDateTime dataHora;
    private double kmPercorridos;
    private Double combustivelLitros;
    private Double noxPpm;
    private Double mpMgM3;
    private Double temperaturaArAdmissao;
    private Double umidadeAbsolutaAr;
    private String observacoes;

    public static LeituraDTO fromEntity(LeituraCO2 l) {
        LeituraDTO dto = new LeituraDTO();
        dto.id                    = l.getId();
        dto.onibusId              = l.getOnibus().getId();
        dto.onibusPrefixo         = l.getOnibus().getPrefixo();
        dto.dataHora              = l.getDataHora();
        dto.kmPercorridos         = l.getKmPercorridos();
        dto.combustivelLitros     = l.getCombustivelLitros();
        dto.noxPpm                = l.getNoxPpm();
        dto.mpMgM3                = l.getMpMgM3();
        dto.temperaturaArAdmissao = l.getTemperaturaArAdmissao();
        dto.umidadeAbsolutaAr     = l.getUmidadeAbsolutaAr();
        dto.observacoes           = l.getObservacoes();
        return dto;
    }

    public Long getId()                                              { return id; }
    public void setId(Long id)                                       { this.id = id; }

    public Long getOnibusId()                                        { return onibusId; }
    public void setOnibusId(Long onibusId)                           { this.onibusId = onibusId; }

    public String getOnibusPrefixo()                                 { return onibusPrefixo; }
    public void setOnibusPrefixo(String onibusPrefixo)               { this.onibusPrefixo = onibusPrefixo; }

    public LocalDateTime getDataHora()                               { return dataHora; }
    public void setDataHora(LocalDateTime dataHora)                  { this.dataHora = dataHora; }

    public double getKmPercorridos()                                 { return kmPercorridos; }
    public void setKmPercorridos(double kmPercorridos)               { this.kmPercorridos = kmPercorridos; }

    public Double getCombustivelLitros()                             { return combustivelLitros; }
    public void setCombustivelLitros(Double combustivelLitros)       { this.combustivelLitros = combustivelLitros; }

    public Double getNoxPpm()                                        { return noxPpm; }
    public void setNoxPpm(Double noxPpm)                             { this.noxPpm = noxPpm; }

    public Double getMpMgM3()                                        { return mpMgM3; }
    public void setMpMgM3(Double mpMgM3)                             { this.mpMgM3 = mpMgM3; }

    public Double getTemperaturaArAdmissao()                         { return temperaturaArAdmissao; }
    public void setTemperaturaArAdmissao(Double temperaturaArAdmissao) { this.temperaturaArAdmissao = temperaturaArAdmissao; }

    public Double getUmidadeAbsolutaAr()                             { return umidadeAbsolutaAr; }
    public void setUmidadeAbsolutaAr(Double umidadeAbsolutaAr)       { this.umidadeAbsolutaAr = umidadeAbsolutaAr; }

    public String getObservacoes()                                   { return observacoes; }
    public void setObservacoes(String observacoes)                   { this.observacoes = observacoes; }
}
