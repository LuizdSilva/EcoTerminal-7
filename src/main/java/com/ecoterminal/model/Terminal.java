package com.ecoterminal.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "terminal")
public class Terminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(unique = true, length = 30)
    private String codigo;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @OneToMany(mappedBy = "terminal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Onibus> onibus = new ArrayList<>();

    public Terminal() {}

    public Terminal(String nome, String codigo, String cidade, String estado) {
        this.nome   = nome;
        this.codigo = codigo;
        this.cidade = cidade;
        this.estado = estado;
    }

    public void addOnibus(Onibus o) {
        onibus.add(o);
        o.setTerminal(this);
    }

    public void removeOnibus(Onibus o) {
        onibus.remove(o);
        o.setTerminal(null);
    }

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public String getNome()                    { return nome; }
    public void setNome(String nome)           { this.nome = nome; }

    public String getCodigo()                  { return codigo; }
    public void setCodigo(String codigo)       { this.codigo = codigo; }

    public String getCidade()                  { return cidade; }
    public void setCidade(String cidade)       { this.cidade = cidade; }

    public String getEstado()                  { return estado; }
    public void setEstado(String estado)       { this.estado = estado; }

    public List<Onibus> getOnibus()            { return onibus; }
    public void setOnibus(List<Onibus> onibus) { this.onibus = onibus; }
}
