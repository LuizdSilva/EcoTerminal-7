package com.ecoterminal.enums;


public enum TipoOnibus {

    MINIBUS("Miniônibus 8,4–9,0m"),
    MIDIBUS("Midiônibus 9,6–11,5m"),
    BASICO("Básico 11,5–12,5m"),
    PADRON("Padron 12,5m"),
    PADRON_15M("Padron 15m"),
    ARTICULADO("Articulado 18,3m"),
    ARTICULADO_23M("Articulado 23m"),
    BIARTICULADO("Biarticulado ≤27m"),
    TROLEBUS("Trólebus / Elétrico");

    private final String descricao;

    TipoOnibus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}

