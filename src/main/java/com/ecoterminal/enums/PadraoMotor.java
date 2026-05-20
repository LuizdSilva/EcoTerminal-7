package com.ecoterminal.enums;

public enum PadraoMotor {

    P5_EURO_III("PROCONVE P-5 / Euro III"),
    P7_EURO_V("PROCONVE P-7 / Euro V"),
    P8_EURO_VI("PROCONVE P-8 / Euro VI"),
    ELETRICO("Motor Elétrico / Trólebus");

    private final String descricao;

    PadraoMotor(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}