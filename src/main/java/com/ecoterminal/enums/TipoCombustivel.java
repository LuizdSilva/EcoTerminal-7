package com.ecoterminal.enums;

public enum TipoCombustivel {

    DIESEL_B7("Diesel B7 (padrão 2016)", 251.0),
    DIESEL_B10("Diesel B10 (padrão 2018)", 243.0),
    DIESEL_B15("Diesel B15 (padrão a partir de 2023)", 230.0),
    DIESEL_B20("Diesel B20", 220.0),
    BIODIESEL_B100("Biodiesel B100 (soja)", 0.0),
    ETANOL_ED95("Etanol ED95 (cana-de-açúcar)", 0.0),
    GNC_FOSSIL("GNC Fóssil (gás natural)", 210.0),
    BIOMETANO("Biometano", 0.0),
    ELETRICIDADE("Eletricidade (mix Brasil)", 0.0);

    private final String descricao;
    private final double efCo2GKwh;

    TipoCombustivel(String descricao, double efCo2GKwh) {
        this.descricao  = descricao;
        this.efCo2GKwh  = efCo2GKwh;
    }

    public String getDescricao()  { return descricao; }
    public double getEfCo2GKwh() { return efCo2GKwh; }
}