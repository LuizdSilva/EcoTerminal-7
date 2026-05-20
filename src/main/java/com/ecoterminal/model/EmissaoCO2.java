package com.ecoterminal.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "emissao_co2")
public class EmissaoCO2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onibus_id", nullable = false)
    private Onibus onibus;

    @Column(nullable = false)
    private int anoReferencia;

    @Column(nullable = false)
    private LocalDate dataCalculo;

    @Column(nullable = false)
    private double co2Toneladas;

    @Column(nullable = false)
    private double mpToneladas;

    @Column(nullable = false)
    private double noxToneladas;

    @Column(nullable = false)
    private double vktKmAno;

    @Column(nullable = false)
    private double consumoEnergiaKwhKm;

    @Column(nullable = false)
    private double efCo2GKwh;

    @Column(nullable = false)
    private double efMpGKm;

    @Column(nullable = false)
    private double efNoxGKm;

    @Column
    private Double reducaoCo2Percentual;

    @Column
    private Boolean conformeCo2;

    @Column
    private Boolean conformeMp;

    @Column
    private Boolean conformeNox;

    public EmissaoCO2() {}

    public EmissaoCO2(Onibus onibus, int anoReferencia, LocalDate dataCalculo,
                      double co2Toneladas, double mpToneladas, double noxToneladas,
                      double vktKmAno, double consumoEnergiaKwhKm,
                      double efCo2GKwh, double efMpGKm, double efNoxGKm) {
        this.onibus              = onibus;
        this.anoReferencia       = anoReferencia;
        this.dataCalculo         = dataCalculo;
        this.co2Toneladas        = co2Toneladas;
        this.mpToneladas         = mpToneladas;
        this.noxToneladas        = noxToneladas;
        this.vktKmAno            = vktKmAno;
        this.consumoEnergiaKwhKm = consumoEnergiaKwhKm;
        this.efCo2GKwh           = efCo2GKwh;
        this.efMpGKm             = efMpGKm;
        this.efNoxGKm            = efNoxGKm;
    }

    public Long getId()                                              { return id; }
    public void setId(Long id)                                       { this.id = id; }

    public Onibus getOnibus()                                        { return onibus; }
    public void setOnibus(Onibus onibus)                             { this.onibus = onibus; }

    public int getAnoReferencia()                                    { return anoReferencia; }
    public void setAnoReferencia(int anoReferencia)                  { this.anoReferencia = anoReferencia; }

    public LocalDate getDataCalculo()                                { return dataCalculo; }
    public void setDataCalculo(LocalDate dataCalculo)                { this.dataCalculo = dataCalculo; }

    public double getCo2Toneladas()                                  { return co2Toneladas; }
    public void setCo2Toneladas(double co2Toneladas)                 { this.co2Toneladas = co2Toneladas; }

    public double getMpToneladas()                                   { return mpToneladas; }
    public void setMpToneladas(double mpToneladas)                   { this.mpToneladas = mpToneladas; }

    public double getNoxToneladas()                                  { return noxToneladas; }
    public void setNoxToneladas(double noxToneladas)                 { this.noxToneladas = noxToneladas; }

    public double getVktKmAno()                                      { return vktKmAno; }
    public void setVktKmAno(double vktKmAno)                         { this.vktKmAno = vktKmAno; }

    public double getConsumoEnergiaKwhKm()                           { return consumoEnergiaKwhKm; }
    public void setConsumoEnergiaKwhKm(double consumoEnergiaKwhKm)   { this.consumoEnergiaKwhKm = consumoEnergiaKwhKm; }

    public double getEfCo2GKwh()                                     { return efCo2GKwh; }
    public void setEfCo2GKwh(double efCo2GKwh)                       { this.efCo2GKwh = efCo2GKwh; }

    public double getEfMpGKm()                                       { return efMpGKm; }
    public void setEfMpGKm(double efMpGKm)                           { this.efMpGKm = efMpGKm; }

    public double getEfNoxGKm()                                      { return efNoxGKm; }
    public void setEfNoxGKm(double efNoxGKm)                         { this.efNoxGKm = efNoxGKm; }

    public Double getReducaoCo2Percentual()                          { return reducaoCo2Percentual; }
    public void setReducaoCo2Percentual(Double reducaoCo2Percentual) { this.reducaoCo2Percentual = reducaoCo2Percentual; }

    public Boolean getConformeCo2()                                  { return conformeCo2; }
    public void setConformeCo2(Boolean conformeCo2)                  { this.conformeCo2 = conformeCo2; }

    public Boolean getConformeMp()                                   { return conformeMp; }
    public void setConformeMp(Boolean conformeMp)                    { this.conformeMp = conformeMp; }

    public Boolean getConformeNox()                                  { return conformeNox; }
    public void setConformeNox(Boolean conformeNox)                  { this.conformeNox = conformeNox; }
}
