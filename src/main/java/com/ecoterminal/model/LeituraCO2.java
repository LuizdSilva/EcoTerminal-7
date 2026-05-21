package com.ecoterminal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "leitura_co2")
public class LeituraCO2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onibus_id", nullable = false)
    private Onibus onibus;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private double kmPercorridos;

    @Column
    private Double combustivelLitros;

    @Column
    private Double noxPpm;

    @Column
    private Double mpMgM3;

    @Column
    private Double temperaturaArAdmissao;

    @Column
    private Double umidadeAbsolutaAr;

    @Column(length = 500)
    private String observacoes;

    public LeituraCO2() {}

    public LeituraCO2(Onibus onibus, LocalDateTime dataHora, double kmPercorridos) {
        this.onibus        = onibus;
        this.dataHora      = dataHora;
        this.kmPercorridos = kmPercorridos;
    }

    public Long getId(){
         return id; }
    public void setId(Long id){ 
        this.id = id; }

    public Onibus getOnibus(){ 
        return onibus; }
    public void setOnibus(Onibus onibus){ 
        this.onibus = onibus; }

    public LocalDateTime getDataHora(){
         return dataHora; }
    public void setDataHora(LocalDateTime dataHora){
         this.dataHora = dataHora; }

    public double getKmPercorridos(){ 
        return kmPercorridos; }
    public void setKmPercorridos(double kmPercorridos){ 
        this.kmPercorridos = kmPercorridos; }

    public Double getCombustivelLitros(){
         return combustivelLitros; }
    public void setCombustivelLitros(Double combustivelLitros){
        this.combustivelLitros = combustivelLitros; }

    public Double getNoxPpm(){ 
        return noxPpm; }
    public void setNoxPpm(Double noxPpm){ 
        this.noxPpm = noxPpm; }

    public Double getMpMgM3(){ 
        return mpMgM3; }
    public void setMpMgM3(Double mpMgM3){ 
        this.mpMgM3 = mpMgM3; }

    public Double getTemperaturaArAdmissao(){ 
        return temperaturaArAdmissao; }
    public void setTemperaturaArAdmissao(Double temperaturaArAdmissao){ 
        this.temperaturaArAdmissao = temperaturaArAdmissao; }

    public Double getUmidadeAbsolutaAr(){ 
        return umidadeAbsolutaAr; }
    public void setUmidadeAbsolutaAr(Double umidadeAbsolutaAr){
         this.umidadeAbsolutaAr = umidadeAbsolutaAr; }

    public String getObservacoes(){
         return observacoes; }
    public void setObservacoes(String observacoes){ 
        this.observacoes = observacoes; }
}
