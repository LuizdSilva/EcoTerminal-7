package com.ecoterminal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_emissao")
public class AlertaEmissao {

    public enum TipoAlerta {
        NOX_ACIMA_LIMITE,
        MP_ACIMA_LIMITE,
        CO2_META_NAO_ATINGIDA,
        CONFORMIDADE_LEI_16802
    }

    public enum Severidade {
        INFO, AVISO, CRITICO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onibus_id", nullable = false)
    private Onibus onibus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAlerta tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severidade severidade;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column
    private Double valorMedido;

    @Column
    private Double valorLimite;

    @Column(nullable = false)
    private boolean reconhecido = false;

    public AlertaEmissao() {}

    public AlertaEmissao(Onibus onibus, TipoAlerta tipo, Severidade severidade,
                         LocalDateTime dataHora, String mensagem,
                         Double valorMedido, Double valorLimite) {
        this.onibus      = onibus;
        this.tipo        = tipo;
        this.severidade  = severidade;
        this.dataHora    = dataHora;
        this.mensagem    = mensagem;
        this.valorMedido = valorMedido;
        this.valorLimite = valorLimite;
    }

    public Long getId()                            { return id; }
    public void setId(Long id)                     { this.id = id; }

    public Onibus getOnibus()                      { return onibus; }
    public void setOnibus(Onibus onibus)           { this.onibus = onibus; }

    public TipoAlerta getTipo()                    { return tipo; }
    public void setTipo(TipoAlerta tipo)           { this.tipo = tipo; }

    public Severidade getSeveridade()                    { return severidade; }
    public void setSeveridade(Severidade severidade)     { this.severidade = severidade; }

    public LocalDateTime getDataHora()                   { return dataHora; }
    public void setDataHora(LocalDateTime dataHora)      { this.dataHora = dataHora; }

    public String getMensagem()                    { return mensagem; }
    public void setMensagem(String mensagem)       { this.mensagem = mensagem; }

    public Double getValorMedido()                       { return valorMedido; }
    public void setValorMedido(Double valorMedido)       { this.valorMedido = valorMedido; }

    public Double getValorLimite()                       { return valorLimite; }
    public void setValorLimite(Double valorLimite)       { this.valorLimite = valorLimite; }

    public boolean isReconhecido()                       { return reconhecido; }
    public void setReconhecido(boolean reconhecido)      { this.reconhecido = reconhecido; }
}
