-- ═══════════════════════════════════════════════════════════════════
-- EcoTerminal — schema.sql
-- Banco: PostgreSQL (prod) / H2 (dev — usar schema-h2.sql separado)
-- Corrigido: AUTO_INCREMENT → BIGSERIAL | comentários inline removidos
-- ═══════════════════════════════════════════════════════════════════

-- ── Limpa na ordem inversa das dependências ──────────────────────────
DROP TABLE IF EXISTS alerta_emissao;
DROP TABLE IF EXISTS emissao_co2;
DROP TABLE IF EXISTS leitura_co2;
DROP TABLE IF EXISTS onibus;
DROP TABLE IF EXISTS terminal;

-- ═══════════════════════════════════════════════════════════════════
-- 1. TERMINAL
-- ═══════════════════════════════════════════════════════════════════
CREATE TABLE terminal (
    id      BIGSERIAL    NOT NULL,
    nome    VARCHAR(120) NOT NULL,
    codigo  VARCHAR(30)  UNIQUE,
    cidade  VARCHAR(100),
    estado  VARCHAR(2),
    PRIMARY KEY (id)
);

-- ═══════════════════════════════════════════════════════════════════
-- 2. ONIBUS
-- ManyToOne → terminal
-- Enums armazenados como STRING
-- ═══════════════════════════════════════════════════════════════════
CREATE TABLE onibus (
    id                   BIGSERIAL    NOT NULL,
    prefixo              VARCHAR(20)  NOT NULL UNIQUE,
    tipo                 VARCHAR(30)  NOT NULL,
    padrao_motor         VARCHAR(30)  NOT NULL,
    combustivel          VARCHAR(30)  NOT NULL,
    tem_ar_condicionado  BOOLEAN      NOT NULL DEFAULT FALSE,
    km_anuais            DOUBLE PRECISION NOT NULL DEFAULT 0,
    ano_fabricacao       INT          NOT NULL,
    terminal_id          BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_onibus_terminal
        FOREIGN KEY (terminal_id) REFERENCES terminal(id)
);

-- ═══════════════════════════════════════════════════════════════════
-- 3. LEITURA_CO2
-- Leituras de telemetria por ônibus — ManyToOne → onibus
-- ═══════════════════════════════════════════════════════════════════
CREATE TABLE leitura_co2 (
    id                      BIGSERIAL        NOT NULL,
    onibus_id               BIGINT           NOT NULL,
    data_hora               TIMESTAMP        NOT NULL,
    km_percorridos          DOUBLE PRECISION NOT NULL,
    combustivel_litros      DOUBLE PRECISION,
    nox_ppm                 DOUBLE PRECISION,
    mp_mg_m3                DOUBLE PRECISION,
    temperatura_ar_admissao DOUBLE PRECISION,
    umidade_absoluta_ar     DOUBLE PRECISION,
    observacoes             VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT fk_leitura_onibus
        FOREIGN KEY (onibus_id) REFERENCES onibus(id)
);

CREATE INDEX idx_leitura_onibus_id ON leitura_co2(onibus_id);
CREATE INDEX idx_leitura_data_hora ON leitura_co2(data_hora);

-- ═══════════════════════════════════════════════════════════════════
-- 4. EMISSAO_CO2
-- Resultado calculado por ônibus/ano — ManyToOne → onibus
-- ═══════════════════════════════════════════════════════════════════
CREATE TABLE emissao_co2 (
    id                      BIGSERIAL        NOT NULL,
    onibus_id               BIGINT           NOT NULL,
    ano_referencia          INT              NOT NULL,
    data_calculo            DATE             NOT NULL,

    co2_toneladas           DOUBLE PRECISION NOT NULL,
    mp_toneladas            DOUBLE PRECISION NOT NULL,
    nox_toneladas           DOUBLE PRECISION NOT NULL,

    vkt_km_ano              DOUBLE PRECISION NOT NULL,
    consumo_energia_kwh_km  DOUBLE PRECISION NOT NULL,
    ef_co2_g_kwh            DOUBLE PRECISION NOT NULL,
    ef_mp_g_km              DOUBLE PRECISION NOT NULL,
    ef_nox_g_km             DOUBLE PRECISION NOT NULL,

    reducao_co2_percentual  DOUBLE PRECISION,
    conforme_co2            BOOLEAN,
    conforme_mp             BOOLEAN,
    conforme_nox            BOOLEAN,

    PRIMARY KEY (id),
    CONSTRAINT fk_emissao_onibus
        FOREIGN KEY (onibus_id) REFERENCES onibus(id),
    CONSTRAINT uq_emissao_onibus_ano
        UNIQUE (onibus_id, ano_referencia)
);

CREATE INDEX idx_emissao_onibus_id      ON emissao_co2(onibus_id);
CREATE INDEX idx_emissao_ano_referencia ON emissao_co2(ano_referencia);

-- ═══════════════════════════════════════════════════════════════════
-- 5. ALERTA_EMISSAO
-- Alertas gerados pelo AlertaService — ManyToOne → onibus
-- ═══════════════════════════════════════════════════════════════════
CREATE TABLE alerta_emissao (
    id           BIGSERIAL    NOT NULL,
    onibus_id    BIGINT       NOT NULL,
    tipo         VARCHAR(40)  NOT NULL,
    severidade   VARCHAR(10)  NOT NULL,
    data_hora    TIMESTAMP    NOT NULL,
    mensagem     VARCHAR(500) NOT NULL,
    valor_medido DOUBLE PRECISION,
    valor_limite DOUBLE PRECISION,
    reconhecido  BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_alerta_onibus
        FOREIGN KEY (onibus_id) REFERENCES onibus(id)
);

CREATE INDEX idx_alerta_onibus_id   ON alerta_emissao(onibus_id);
CREATE INDEX idx_alerta_reconhecido ON alerta_emissao(reconhecido);
