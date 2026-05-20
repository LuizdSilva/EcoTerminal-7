package com.ecoterminal.model;

import com.ecoterminal.enums.PadraoMotor;
import com.ecoterminal.enums.TipoCombustivel;
import com.ecoterminal.enums.TipoOnibus;
import jakarta.persistence.*;

@Entity
@Table(name = "onibus")
public class Onibus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String prefixo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOnibus tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PadraoMotor padraoMotor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCombustivel combustivel;

    @Column(nullable = false)
    private boolean temArCondicionado;

    @Column(nullable = false)
    private double kmAnuais;

    @Column(nullable = false)
    private int anoFabricacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id", nullable = false)
    private Terminal terminal;

    public Onibus() {}

    public Onibus(String prefixo, TipoOnibus tipo, PadraoMotor padraoMotor,
                  TipoCombustivel combustivel, boolean temArCondicionado,
                  double kmAnuais, int anoFabricacao, Terminal terminal) {
        this.prefixo           = prefixo;
        this.tipo              = tipo;
        this.padraoMotor       = padraoMotor;
        this.combustivel       = combustivel;
        this.temArCondicionado = temArCondicionado;
        this.kmAnuais          = kmAnuais;
        this.anoFabricacao     = anoFabricacao;
        this.terminal          = terminal;
    }

    public Long getId()                                      { return id; }
    public void setId(Long id)                               { this.id = id; }

    public String getPrefixo()                               { return prefixo; }
    public void setPrefixo(String prefixo)                   { this.prefixo = prefixo; }

    public TipoOnibus getTipo()                              { return tipo; }
    public void setTipo(TipoOnibus tipo)                     { this.tipo = tipo; }

    public PadraoMotor getPadraoMotor()                      { return padraoMotor; }
    public void setPadraoMotor(PadraoMotor padraoMotor)      { this.padraoMotor = padraoMotor; }

    public TipoCombustivel getCombustivel()                  { return combustivel; }
    public void setCombustivel(TipoCombustivel combustivel)  { this.combustivel = combustivel; }

    public boolean isTemArCondicionado()                             { return temArCondicionado; }
    public void setTemArCondicionado(boolean temArCondicionado)      { this.temArCondicionado = temArCondicionado; }

    public double getKmAnuais()                              { return kmAnuais; }
    public void setKmAnuais(double kmAnuais)                 { this.kmAnuais = kmAnuais; }

    public int getAnoFabricacao()                            { return anoFabricacao; }
    public void setAnoFabricacao(int anoFabricacao)          { this.anoFabricacao = anoFabricacao; }

    public Terminal getTerminal()                            { return terminal; }
    public void setTerminal(Terminal terminal)               { this.terminal = terminal; }
}
